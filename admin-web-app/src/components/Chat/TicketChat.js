import React, {Component} from 'react';
import {Button, ActivityIndicator, Text, View, TextInput} from 'react-native';
import {URL} from '../shared/const';
import {getAuth} from '../shared/auth';
import {setState} from '../shared/GlobalState';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import { Link } from 'react-router-dom';

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
    this.makeApiCall();
  }

  async makeApiCall() {
    var url = URL;
		url += '/messages/' + this.state.idTicket;
    await fetch(url, {method:'GET', headers: getAuth()})
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
    this.makeApiCall();
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

    return(
		<View>
        <Button
          onPress = { function doNothing() {} }
          disabled = {true}
          title = {"Chat history of " + this.props.tName + " in " + this.props.name}
        />

        {this.renderChat()}

        <TextInput
          placeholder = "Message"
          style = {{height: 40, borderColor: 'gray',borderWidth: 1}}
          onChangeText = {(text) => this.setState({message: text})} />

        <Button onPress = { this.onSendPressed.bind(this) } title = "Send" color = "#0c3868" />
				<Link to={ '/projects/' + this.state.project + '/tickets/' + this.props.match.params.id }>
          <Button
            onPress = { function doNothing() {} }
            title = "Back to Tickets"
            color = "#0e4a80" />
				</Link>
      </View>
    );
  }
}
