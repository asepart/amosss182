import React, { Component } from 'react';
import { StyleSheet, ScrollView, ActivityIndicator, Text, View, TouchableOpacity } from 'react-native';
import { ticket } from '../Chat/sendMessages';
import { key } from './keyValid';
import { URL } from '../Login/const';
import { getAuth } from '../Login/auth';
import styles from '../Login/Design';
import { setState } from '../Login/state';
import { setTicketID } from '../Chat/sendMessages';
import { StackNavigator } from 'react-navigation'

export default class TicketView extends Component {

  static navigationOptions = {
    title: 'Ticket Details',
    headerStyle: {
      backgroundColor: '#8eacbb'
    },
    headerTitleStyle: {
      color: '#FFF'
    }
  }

  constructor(props) {
    super(props);
    this.state = {
      isLoading: true,
      ticketDetail: "",
      idTicket: ""

    };
  }

  onChatPressed() {
    setTicketID(this.state.idTicket);
    const { navigate } = this.props.navigation;
    navigate("Seventh", { name: "GetMessages" })
  }

  onAcceptPressed() {
    let ticketID = this.props.navigation.state.params.id;    
    var response = fetch(URL + '/projects/' + key + '/tickets/'+ ticketID + '/accept', {
      method: 'POST',
      headers: getAuth()
    })
  }

  async GetTicketInfo() {
    let ticketID = this.props.navigation.state.params.id;
    this.setState({
      idTicket: ticketID
    })
    fetch(URL + '/projects/' + key + '/tickets/' + ticketID, { method: 'GET', headers: getAuth() })
      .then((response) => response.json())
      .then((responseJson) => {
        this.setState({
          isLoading: false,
          ticketDetail: responseJson
        }, function () { });
      }).catch((error) => {
        console.error(error);
      });
  }

  componentDidMount() {
    this.GetTicketInfo();

  }
  render() {
    var { params } = this.props.navigation.state;
    if (this.state.isLoading) {
      return (
        <View style={{ flex: 1, padding: 20 }}>
          <ActivityIndicator />
        </View>
      )
    }
    return (
      <View style={styles.container}>
        <TouchableOpacity
          onPress={this.onChatPressed.bind(this)}
          style={styles.buttonContainer}>
          <Text style={styles.buttonText}>Chat</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={this.onAcceptPressed.bind(this)}
          style={styles.buttonContainer}>
          <Text style={styles.buttonText}>Accept</Text>
        </TouchableOpacity>
        <Text style={styles.text}>
          Id: {this.state.ticketDetail.id}
        </Text>
        <Text style={styles.text}>
          Key: {this.state.ticketDetail.projectKey}
        </Text>
        <Text style={styles.text}>
          Required Observations: {this.state.ticketDetail.requiredObservations}
        </Text>
        <Text style={styles.text}>
          Category: {this.state.ticketDetail.ticketCategory}
        </Text>
        <Text style={styles.text}>
          Ticket Name: {this.state.ticketDetail.ticketName}
        </Text>
        <Text style={styles.text}>
          Ticket Status: {this.state.ticketDetail.ticketStatus}
        </Text>
        <Text style={styles.text}>
          Summary: {this.state.ticketDetail.ticketSummary}
        </Text>
        <ScrollView style={styles.containerScroll}>
          <Text style={styles.textLarge}>
            Description:
        </Text>
          <Text style={styles.text} >
            {this.state.ticketDetail.ticketDescription}
          </Text>
        </ScrollView>
      </View>

    );
  }
}
