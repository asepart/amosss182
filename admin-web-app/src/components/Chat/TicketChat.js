import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput, ScrollView, Dimensions} from 'react-native';
import {URL} from '../shared/const';
import {getAuth} from '../shared/auth';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
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
    fetch(URL + '/projects/' + this.props.match.params.project + '/tickets/' + this.state.idTicket, {method:'GET', headers: getAuth()})
    .then((response) => response.json())
    .then((responseJson) => {
      this.setState({
        isLoading: false,
        tName: responseJson.ticketName,
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
          <Text style={{fontWeight: 'bold'}}>
            [{news.content.slice(18,27)} {news.sender}: {news.content.slice(29)}
          </Text>
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
            tmp_projectName = this.state.allProjects[i].projectName;
          }
        }
      }
    }
    else {
      tmp_ticketName = this.props.tName;
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

        <TextInput
          placeholder = "Message"
          style = {{height: 40, borderColor: 'gray',borderWidth: 1}}
          onChangeText = {(text) => this.setState({message: text})}
          ref = {input => { this.textInput = input }}
          onKeyPress = {(event) => {
            if (event.key === 'Enter') {
              this.onSendPressed();
            }
          }}
        />

        <Button onPress = { this.onSendPressed.bind(this) } title = "Send" color = "#0c3868" disabled = {!buttonEnabled}/>
      </View>
    );
  }
}
