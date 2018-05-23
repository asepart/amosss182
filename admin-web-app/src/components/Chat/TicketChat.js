import React, {Component} from 'react';
import {Button, FlatList, ActivityIndicator, Text, View, TextInput} from 'react-native';
import {URL} from '../shared/const';
import {getAuth} from '../shared/auth';
import {setState} from '../shared/GlobalState';
import {setMsg, sendMessage, setTicketID} from './sendMessages'
import ReactTable from 'react-table';
import 'react-table/react-table.css';

export default class TicketChat extends Component {

  constructor(props){
    super(props);
    this.state = {
      isLoading: true,
      message: "",
      idTicket: this.props.id,
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

  showTicketList() {
		setState({
			isAuth: true,
			show: 'showTickets',
			param: this.props.project,
			name: this.props.name
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

    const columns = [
      {
        Header: 'SENDER',
        accessor: 'sender',
        width: 150,
      }, {
        Header: 'CONTENT',
        accessor: 'content',
      }
    ]

    return(
      <View>
        <Button onPress = { this.showTicketList.bind(this) } title = "Back to Tickets" color = "#0e4a80" />

        <FlatList
          //using flatlist would be a better view - no clue why it does not show
          data={this.state.chatHistory}
          renderItem={({item}) => <Text>{item.sender} : {item.content}</Text>}
          keyExtractor={(item) => item.id}
        />

        <ReactTable
          //this is the ugly alternative
          data={this.state.chatHistory}
          columns={columns}
        />

        <TextInput
          placeholder = "Message"
          style = {{height: 40, borderColor: 'gray',borderWidth: 1}}
          onChangeText = {(text) => this.setState({message: text})} />

        <Button onPress = { this.onSendPressed.bind(this) } title = "Send" color = "#0e4a80" />
      </View>
    );
  }
}
