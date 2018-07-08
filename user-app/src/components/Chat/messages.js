import React, {Component} from 'react';
import { Platform, ActivityIndicator, View, Linking, Button } from 'react-native';
import {URL} from '../Login/const';
import {getAuth, username} from '../Login/auth';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import {ticket} from './sendMessages';
import { GiftedChat } from 'react-native-gifted-chat';
import CustomActions from './customActions';
import Icon from 'react-native-vector-icons/FontAwesome';

var limit = 30;

export default class Messages extends Component {

	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state;
		return {
		title: 'Chat',
		headerStyle: {
			backgroundColor:'#5daedb',
			paddingRight: 15
		},
		headerTitleStyle: {
			color:'#FFF'
		},
		headerRight: <Icon name="user" size={30} color="#FFF"  onPress={ () => params.update() } />
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
		this.props.navigation.setParams({ update: this.updateUser });
		this.makeApiCall();
		this.listenForNewMessages();
	}

	componentWillUnmount() {
		clearInterval(this.interval);
	}
	

	updateUser = () => {
			const { navigate } = this.props.navigation;
			navigate("Thirteenth", { name: "UserInfo" });
	}

	async makeApiCall() {
		return await fetch(URL + '/messages/' + ticket + '?limit=30', {method:'GET', headers: getAuth()})
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

	async onLoadEarlierPressed() {
		var newLimit = limit * 2;
		limit = newLimit;
		return await fetch(URL + '/messages/' + ticket + '?limit=' + newLimit, {method:'GET', headers: getAuth()})
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

	sleep(ms) {
		return new Promise(resolve => setTimeout(resolve, ms));
	}

	async listenForNewMessages() {
		while (true){
			this.makeApiCall();
			await this.sleep(10000);
			//stop after leaving chat
			if (this.props.navigation.state.params.name !== 'GetMessages')
				return;
		}
	}

	async onSendPressed() {
		setMsg(this.state.message);
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
				if(message.attachment === null) {
					return {
						_id: message.id,
						text: message.content,
						user: Object.assign({_id: message.sender, name: message.sender}),
						createdAt: new Date(parseInt(message.timestamp)),
					};
				}
				else {
					return {
						_id: message.id,
						text: message.content,
						user: Object.assign({_id: message.sender, name: message.sender}),
						createdAt: new Date(parseInt(message.timestamp)),
						image: URL + '/files/' + message.attachment + '?thumbnail=false',
					};
				}
		});

		//GiftedChat somehow shows the newest message at the top right now
		//inverted={false} does solve this but also breaks the position of the date
		//better fix right now: reversing the messages array before rendering via GiftedChat
		messages.reverse();

		return(
			<GiftedChat
				messages={messages}
				loadEarlier={true}
				returnKeyType="send"
				onSubmitEditing={this.onSendPressed.bind(this)}
				onLoadEarlier={this.onLoadEarlierPressed.bind(this)}
				onInputTextChanged={(text) => this.setState({message: text})}
				onSend={this.onSendPressed.bind(this)}
				showAvatarForEveryMessage={true}
				renderActions={ () => this.renderCustomActions(this.props)}
				user={{
					_id: username,
					name: username,
				}}
			/>
		);
	}
}
