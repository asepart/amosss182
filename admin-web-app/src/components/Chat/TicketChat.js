import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput, ScrollView, Dimensions} from 'react-native';
import {URL, FileSelector} from '../shared/const';
import {getAuth, getAuthForPost, username, psw} from '../shared/auth';
import {setMsg, sendMessage, setTicketID, setAttachment, sendAttachment} from './sendMessages';
import {getUpdateBoolean, setUpdateBoolean} from '../shared/GlobalState';
import ChatMessage from './ChatMessage';

export default class TicketChat extends Component {

	constructor(props){
		super(props);
		this.state = {
			isLoading: true,
			message: "",
			idTicket: this.props.match.params.id,
			chatHistory: [],
		}

	}

	componentDidMount() {
		if(this.props.name === undefined || this.props.tName === undefined) {
			this.fetchTicketName();
			this.fetchProjectName();
		}

		this.fetchMessages();
		this.listenForNewMessages();
	}

	componentWillUnmount() {
		//clearInterval(this.interval);
	}

	componentDidUpdate() {
		if(getUpdateBoolean() === true) {
			this.fetchMessages();
			setUpdateBoolean(false);
		}
		this.textInput.focus();
	}

	fetchProjectName() {
		fetch(URL + '/projects/', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				allProjects: responseJson,
			}, function(){});
		})
		.catch((error) =>{
			console.error(error);
		});
	}

	fetchTicketName() {
		fetch(URL + '/tickets/' + this.state.idTicket, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				tName: responseJson.name,
			}, function(){});
		})
		.catch((error) =>{
			console.error(error);
		});
	}

	fetchMessages() {
		fetch(URL + '/messages/' + this.state.idTicket, {method:'GET', headers: getAuth(), timeout: 0})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				chatHistory: responseJson,
			}, function(){});
		})
		.catch((error) =>{
			console.error(error);
		});
	}

	async listenForNewMessages() {
		while (true){
			console.log("fetch " + (new Date()).toISOString());
			var response = await fetch(URL + '/listen/' + this.state.idTicket, {
				method: 'GET',
				headers: getAuth()
			})
			switch (response.status) {
				case 200:
					this.setState({
						isLoading: false,
						chatHistory: response,
					});
					break;
				default:
					console.error("Error:" + response.status);
					console.error(response.text);
			}
		}
	}

	async onSendPressed() {
		setMsg(this.state.message);
		setTicketID(this.state.idTicket);
		sendMessage();
		this.fetchMessages();

		this.textInput.clear();
		this.state.message = "";
		setUpdateBoolean(true);
	}

	handleFile(selectorFiles: FileList) {
		var files = selectorFiles;

		/*
		//declare new FormData since files[0] maybe is not FormData
		const data = new FormData();
		var reader = new FileReader();

		//tried two ways to get path to file
		var path = (window.URL || window.webkitURL).createObjectURL(files[0]);
		var path2 = reader.readAsDataURL(files[0]));
		console.log('createObjectURL: ' + path);
		console.log("readAsDataURL: " + path2);

		//tried appending File Object itself
		data.append('file', files[0]);

		//tried appending with various file paths
		data.append('file', {
			uri: path,
			type: files[0].type,
			name: files[0].name
		});

		data.append('file', {
			uri: path2,
			type: files[0].type,
			name: files[0].name
		});
		*/

		//use this for hardcoded tests to dev stage
		//fetch('http://asepartback-dev.herokuapp.com/files/1', {
		//if you use this and URL points to localhost, remember to set global minio environments (check Sebastian slack message I pinned to dev channel)
		fetch(URL + '/files/' + this.state.idTicket, {
			method:'POST',
			headers: {
				'Accept': 'text/plain',
				'Content-Type': 'multipart/form-data',
				'X-ASEPART-Role': 'Admin',
				'Authorization': 'Basic ' + btoa(username + ":" + psw)
			},
			body: files[0],
			//use this with self declared FormData
			//body: data,
		})
		.then((response) => response.text())
		.then((responseText) => {
			this.setState({}, function() {});
			console.log('response: ' + responseText);
		})
		.catch((error) => {
			console.log('error: ' + error);
		});

		setMsg(files[0].name);
		setAttachment(files[0].name)
		setTicketID(this.state.idTicket);
		sendAttachment();
		this.fetchMessages();
		setUpdateBoolean(true);
	}

	renderChat() {
		var tmp_chat = this.state.chatHistory;
		var tmp_date;
		var date;

		return this.state.chatHistory.map(function(news, id) {
			if(id !== 0) {
				tmp_date = new Date(parseInt(tmp_chat[id-1].timestamp)).toDateString();
				date = new Date(parseInt(tmp_chat[id].timestamp));
			} else {
				tmp_date = new Date(1993, 3, 20);
				date = new Date(parseInt(tmp_chat[id].timestamp));
			}
			return (
				<View key={id}>
					<div>
						{tmp_date !== date.toDateString() ? (
								<Button
									disabled = {true}
									title = {date.toDateString()}
								/>
						) : (
							null
						)}
					</div>

					<div>
						{news.attachment === null ? (
							<Text style={{fontWeight: 'bold'}}>
								[{date.toTimeString().slice(0,8)}] {news.sender}: <ChatMessage>{news.content}</ChatMessage>
							</Text>
						) : (
							<div>
								<Text style={{fontWeight: 'bold'}}>
									[{date.toTimeString().slice(0,8)}] {news.sender}:
								</Text>
								<Text
									onPress = {
										null
									}
								>
									<ChatMessage> {news.content}</ChatMessage>
								</Text>
							</div>
						)}
					</div>

				</View>
			);
		});
	}

	render() {
		if(this.state.isLoading) {
			return(
				<View style={{flex: 1, padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		var tmp_ticketName;
		var tmp_projectName;
		var buttonEnabled = (this.state.message !== '');
		//somehow needed to make ScrollView inside a View scrollable - 33 is about the height of the header
		const screenHeight = Dimensions.get('window').height - 33;

		if(this.props.name === undefined || this.props.tName === undefined) {
			tmp_ticketName = this.state.tName;

			if (this.state.allProjects !== undefined) {
				for(var i=0; i < this.state.allProjects.length; i++) {
					if(this.state.allProjects[i].entryKey === this.props.match.params.project) {
						tmp_projectName = this.state.allProjects[i].name;
					}
				}
			}
		}
		else {
			tmp_ticketName = this.state.tName;
			tmp_projectName = this.props.name;
		}

		return(
			<View style={{height: screenHeight}}>
				<Button
					onPress = { function doNothing() {} }
					disabled = {true}
					title = {"Chat history of " + tmp_ticketName + " in " + tmp_projectName}
				/>

				<ScrollView
					ref = {ref => this.scrollView = ref}
					onContentSizeChange = {(contentWidth, contentHeight) => {
						this.scrollView.scrollToEnd({animated: false});
					}}
				>
					{this.renderChat()}
				</ScrollView>

				<FileSelector
					onLoadFile = {(files:FileList) => this.handleFile(files)}
				/>

				<TextInput
					autoFocus = {true}
					placeholder = "Message"
					style = {{height: 40, borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({message: text})}
					ref = {input => { this.textInput = input }}
					onKeyPress = {(event) => {
						if (event.key === 'Enter' && this.state.message !== '') {
							this.onSendPressed();
						}
					}}
				/>
				<Button onPress = { this.onSendPressed.bind(this) } title = "Send" color = "#0c3868" disabled = {!buttonEnabled}/>
			</View>
		);
	}
}
