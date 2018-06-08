import React, {Component} from 'react';
import { FlatList, ActivityIndicator, Text, View, TextInput, TouchableOpacity, ScrollView, Button } from 'react-native';
import {URL} from '../Login/const';
import {getAuth} from '../Login/auth';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import {ProjectInfo} from '../Projects/projectInfo';
import {TicketView} from '../Projects/ticketView';
import {ticket} from './sendMessages';
import {
	StackNavigator,
  } from 'react-navigation';

export default class GetMessages extends Component {

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

	renderChat() {
    var tmp_chat = this.state.dataSource;
    var tmp_date;

    return this.state.dataSource.map(function(news, id) {
      if(id !== 0) {
        tmp_date = tmp_chat[id-1].content.slice(1,16);
      } else {
        tmp_date = new Date(1993, 3, 20);
      }
      return (
        <View key={id}>
          <View>
            {tmp_date !== news.content.slice(1,16) ? (
                <Button
                  disabled = {true}
                  title = {news.content.slice(1,16)}
                />
            ) : (
              null
            )}
          </View>
          <Text style={{fontWeight: 'bold'}}>{news.sender} [{news.content.slice(18)}</Text>
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

    return(
      <View style={styles.containerChat}>
				<ScrollView
					ref = {ref => this.scrollView = ref}
				  onContentSizeChange = {(contentWidth, contentHeight) => {
						this.scrollView.scrollToEnd({animated: false});
					}}
				>
        	{this.renderChat()}
				</ScrollView>

				<View style={{height: 5}}/>

				<View style={{flexDirection: 'row'}}>
	        <TextInput
						onChangeText={(text) => this.setState({message: text})}
						placeholder="Message"
						underlineColorAndroid="transparent"
						style={styles.sendTextInput}
					/>

	        <TouchableOpacity
	           onPress={this.onSendPressed.bind(this)}
	            style={styles.sendButton}
					>
						<Text style={styles.buttonText}>SEND</Text>
					</TouchableOpacity>
				</View>
      </View>
    );
  }
}
