import React, {Component} from 'react';
import { Platform, ActivityIndicator, View } from 'react-native';
import {URL} from '../Login/const';
import {getAuth, username} from '../Login/auth';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import {ticket} from './sendMessages';
import {StackNavigator} from 'react-navigation';
import { GiftedChat } from 'react-native-gifted-chat';
import CustomActions from './customActions';
import {downloadFile} from './files';

export default class Messages extends Component {

	static navigationOptions= {
		title: 'Chat',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
	}

	constructor(props){
		super(props);
		this.state = {
			isLoading: true,
			message: "",
			error: "",
			//idTicket: ""
		}
	}

	componentDidMount(){
		this.makeApiCall();
		//TODO: following line causes bug, please fix
		//this.interval = setInterval(() => this.listenForNewMessages(), 500);
	}
	
	componentWillUnmount() {
		clearInterval(this.interval);
	}

	async makeApiCall() {
		return fetch(URL + '/messages/' + ticket , {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson,
			}, function(){});
		})
		.catch((error) =>{
			console.error(error);
		});
	}

	async listenForNewMessages() {
	  return fetch(URL + '/listen/' + ticket , {method:'GET', headers: getAuth(), timeout: 0})
	  .then((response) => response.json())
	  .then((responseJson) => {
	    this.setState({
	      isLoading: false,
	      dataSource: responseJson,
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
		sendMessage();
		this.makeApiCall();
	}

renderCustomActions(props) {
	return (
		<CustomActions
		{...props}
		/>
	
	);
}
	onLongPress(ctx, currentMessage) {
	    
		//TODO: add actionsheet here
		downloadFile(currentMessage.text, ticket);
	    
	}
	
	render() {
		if(this.state.isLoading) {
			return(
				<View style={{flex: 1, padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		//remap dataSource to GiftedChat supported object array
		const messages = this.state.dataSource.map((message) => {
			return {
				_id: message.id,
				text: message.content.slice(29),
				user: Object.assign({_id: message.sender, name: message.sender}),
				createdAt: message.content.slice(1,26),
			};
		});

		//GiftedChat somehow shows the newest message at the top right now
		//inverted={false} does solve this but also breaks the position of the date
		//better fix right now: reversing the messages array before rendering via GiftedChat
		messages.reverse();

		return(
			<GiftedChat
				messages={messages}
				onInputTextChanged={(text) => this.setState({message: text})}
				onSend={this.onSendPressed.bind(this)}
				showAvatarForEveryMessage={true}
				renderActions={ () => this.renderCustomActions(this.props)}
				user={{
					_id: username,
					name: username,
				}}
			onLongPress={(ctx, currentMessage) => this.onLongPress(ctx, currentMessage)}
			/>
		);
	}
}
		
