import React, {Component} from 'react';
import { Platform, ActivityIndicator, View, Linking } from 'react-native';
import {URL} from '../Login/const';
import {getAuth, username} from '../Login/auth';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import {ticket} from './sendMessages';
import { GiftedChat } from 'react-native-gifted-chat';
import CustomActions from './customActions';
import {getDownloadLink} from './files';

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

	async onLongPress(ctx, currentMessage) {
	    
		//TODO: for custom actions add actionsheet with additional functions here
		let link = await getDownloadLink(currentMessage.text, ticket);
		if (link != '') {
			Linking.openURL(link);
		}
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
					/*TODO: get URL of thumbnail when file extension problem is solved
					var tmp = fetch(URL + '/files/' + ticket + '/' + message.attachment + '?thumbnail=true', {method:'GET', headers: getAuth()})
					.then((response) => response.text());
					window.alert(JSON.stringify(tmp))
					*/

					return {
						_id: message.id,
						text: message.content,
						user: Object.assign({_id: message.sender, name: message.sender}),
						createdAt: new Date(parseInt(message.timestamp)),
						//TODO: change with real thumbnail URL
						image: 'https://reactjs.org/logo-og.png',
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
=======
	} 
  
  constructor(props){
    super(props);
    this.state ={ isLoading: true, message: "", error: "",
    // idTicket: ""
    }
  }

  async onSendPressed() {
   
  setMsg(this.state.message);
   //if (message === '') {
   // this.setState({error: "message empty"});
  // }
    sendMessage();
    this.makeApiCall();

}



async makeApiCall() {
  
  return fetch(URL + '/messages/' + ticket ,   {method:'GET', headers: getAuth()})
  .then((response) => response.json())
  .then((responseJson) => {

    this.setState({
      isLoading: false,
      dataSource: responseJson,
    }, function(){

    });

  })
  .catch((error) =>{
    console.error(error);
  });

}

  componentDidMount(){
  
    this.makeApiCall();
  }



  render(){

    if(this.state.isLoading){
      return(
        <View style={{flex: 1, padding: 20}}>
          <ActivityIndicator/>
        </View>
      )
    }

    return(
      <View style={styles.container}>
        <FlatList
          data={this.state.dataSource}
          renderItem={({item}) => <Text style={styles.text}>{item.sender} : {item.content} </Text> }
          keyExtractor={(item, index) => index}
        />

        <TextInput  onChangeText={(text) => this.setState({message: text})} placeholder="Message" underlineColorAndroid="transparent" style={styles.inputLong}>
       
        </TextInput>
        
        <TouchableOpacity 
           onPress={this.onSendPressed.bind(this)} 
            style={styles.buttonLargeContainer}>
			
				<Text style={styles.buttonText}>SEND</Text>
          
			</TouchableOpacity>
       
            <Text style={styles.error}>
                    {this.state.error}
                   
				</Text>
      </View>
      
    );
  }
}

