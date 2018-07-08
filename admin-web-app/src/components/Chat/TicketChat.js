import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput, ScrollView, Dimensions} from 'react-native';
import {URL, FileSelector} from '../shared/const';
import {getAuth, getAuthForPost, getAuthForMediaPost, username} from '../shared/auth';
import {setMsg, sendMessage, setTicketID, setAttachment, sendAttachment} from './sendMessages';
import {getUpdateBoolean, setUpdateBoolean} from '../shared/GlobalState';
import {sleep} from '../shared/functions';
import ChatMessage from './ChatMessage';

export default class TicketChat extends Component {

	constructor(props){
		super(props);
		this.state = {
			isLoading: true,
			message: "",
			idTicket: this.props.match.params.id,
			chatHistory: [],
			numChatMsgs: 20,
			newestMsgId: -1
		}
	}

	loadMoreMessages (){
		this.setState({numChatMsgs: (2*this.state.numChatMsgs)});
		this.fetchMessages();
		setUpdateBoolean(true);
	}

	addNewestMessages(response){
		let messages = this.state.chatHistory;
		//add new all messages and remove old ones to stabilize the array length
		response.forEach(msg => {messages.push(msg), messages.shift()});
		return messages;
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
		fetch(URL + '/messages/' + this.state.idTicket + '?limit=' + this.state.numChatMsgs, {method:'GET', headers: getAuth(), timeout: 0})
		.then((response) => response.json())
		.then((responseJson) => {
			let maxID = Math.max.apply(Math, responseJson.map(o => {return Number(o.id)}));
			this.setState({
				isLoading: false,
				chatHistory: responseJson,
				newestMsgId: maxID
			}, function(){});
		})
		.catch((error) =>{
			console.error(error);
		});
	}

	async listenForNewMessages() {
		while(this.state.newestMsgId < 0){
			await sleep(1000);
			console.log("Zzzzz…")
		}
		while (true){
			try {
				var response = await fetch(URL + '/listen/' + this.state.idTicket + '?since=' + this.state.newestMsgId, {
					method: 'GET',
					headers: getAuth()
				});
				switch (response.status) {
					case 200:
						let maxID = Math.max.apply(Math, response.map(o => {return Number(o.id)}));
						this.setState({
							isLoading: false,
							chatHistory: this.addNewestMessages(response),
							newestMsgId: maxID
						});
						break;
					case 503:
						console.log("no msg within 30 sec")
						break;
					default:
						console.error("Error:" + response.status);
						console.error(response.text);
				}

			} catch (e) {
				console.error(e);
			}
		}
	}

	async onSendPressed() {
		setMsg(this.state.message);
		setTicketID(this.state.idTicket);
		sendMessage();

		this.textInput.clear();
		this.state.message = "";
		this.textInput.focus();
	}

	handleFile(selectorFiles: FileList) {
		var files = selectorFiles;
		const formData = new FormData();
		formData.append('file', files[0]);

		//if you use this and URL points to localhost, remember to set global minio environments (check slack dev channel)
		fetch(URL + '/files/' + this.state.idTicket, {
			method:'POST',
			headers: getAuthForMediaPost(),
			body: formData,
		})
		.catch((error) => {
			console.log(error);
		});

		setMsg(URL + '/files/' + this.state.idTicket + '/' + files[0].name);
		setAttachment(files[0].name)
		setTicketID(this.state.idTicket);
		sendAttachment();

		this.fetchMessages();
		setUpdateBoolean(true);
	}

	renderChat(ticket) {
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
									onPress = { () => {}}
									disabled = {true}
									title = {date.toDateString()}
								/>
						) : (
							null
						)}
					</div>

					<div>
						<View style={{flexDirection: 'row'}}>
							<Text style={{fontWeight: 'bold'}}>
								[{date.toTimeString().slice(0,8)}] {news.sender}: {/*just to get a space*/}
							</Text>
							<ChatMessage msg={news} ticket={ticket}/>
						</View>
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
				<Button
					onPress = { this.loadMoreMessages.bind(this) }
					title = {"Show more Messages (" + (this.state.numChatMsgs * 2) + ')'}
				/>
				<ScrollView
					ref = {ref => this.scrollView = ref}
					onContentSizeChange = {(contentWidth, contentHeight) => {
						this.scrollView.scrollToEnd({animated: false});
					}}
				>
					{this.renderChat(this.state.idTicket)}
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
