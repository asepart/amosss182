import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput, ScrollView, Dimensions} from 'react-native';
import {URL, FileSelector} from '../shared/const';
import {getAuth} from '../shared/auth';
import {setMsg, sendMessage, setTicketID} from './sendMessages';
import {getUpdateBoolean, setUpdateBoolean} from '../shared/GlobalState';

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

	async fetchMessages() {
		await fetch(URL + '/messages/' + this.state.idTicket, {method:'GET', headers: getAuth()})
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

	async onSendPressed() {
		var tmp = new Date();
		//+1 is needed, since getMonth returns 0-11
		var date = tmp.toDateString();
		var time = tmp.toTimeString().slice(0,8);
		var timestamp = "[" + date + ", " + time + "]";

		setMsg(timestamp + ": " + this.state.message);
		setTicketID(this.state.idTicket);
		sendMessage();
		this.fetchMessages();

		this.textInput.clear();
		this.state.message = "";
		setUpdateBoolean(true);
	}

	handleFile(selectorFiles: FileList) {
		var files = selectorFiles;
		//test upload
		fetch("https://putsreq.com/PGUfoPMSIL4OjwGnpU4M", {
				method: 'POST',
				//headers: getAuth(),
				body: files[0],
		})

		//TODO: write actual upload after backend is finished
		var tmp = new Date();
		var date = tmp.toDateString();
		var time = tmp.toTimeString().slice(0,8);
		var timestamp = "[" + date + ", " + time + "]";
		setMsg(timestamp + ": Test - You wanted to upload this - " + files[0].name);
		setTicketID(this.state.idTicket);
		sendMessage();
		this.fetchMessages();
		setUpdateBoolean(true);
	}

	renderChat() {
		var tmp_chat = this.state.chatHistory;
		var tmp_date;

		return this.state.chatHistory.map(function(news, id) {
			if(id !== 0) {
				tmp_date = tmp_chat[id-1].content.slice(1,16);
			} else {
				tmp_date = new Date(1993, 3, 20);
			}
			return (
				<View key={id}>
					<div>
						{tmp_date !== news.content.slice(1,16) ? (
								<Button
									disabled = {true}
									title = {news.content.slice(1,16)}
								/>
						) : (
							null
						)}
					</div>

					<div>
						{news.content.search("http") === -1 ? (
							<Text style={{fontWeight: 'bold'}}>
								[{news.content.slice(18,27)} {news.sender}: {news.content.slice(29)}
							</Text>
						) : (
							<div>
								<Text style={{fontWeight: 'bold'}}>
									[{news.content.slice(18,27)} {news.sender}:
								</Text>
								<a href={news.content.slice(29)}> {news.content.slice(29)}</a>
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
