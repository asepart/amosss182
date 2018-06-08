import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput} from 'react-native';
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
    setMsg(this.state.message);
    setTicketID(this.state.idTicket);
    sendMessage();
    this.fetchMessages();

    this.textInput.clear();
    this.state.message = "";
    setUpdateBoolean(true);
  }

  renderChat() {
    return this.state.chatHistory.map(function(news, id){
      return(
        <View key={id}>
          <Text style={{fontWeight: 'bold'}}>{news.sender} : {news.content}</Text>
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
		<View>
        <Button
          onPress = { function doNothing() {} }
          disabled = {true}
          title = {"Chat history of " + tmp_ticketName + " in " + tmp_projectName}
        />

        {this.renderChat()}

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
