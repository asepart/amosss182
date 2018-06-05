import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Picker} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {URL} from '../Login/const';
import {ticket, setTicketID} from '../Chat/sendMessages';
import {key} from '../Projects/keyValid';
import {getAuthForPost} from '../Login/auth';

var pickerPlaceholder = "Outcome";
export var tickID = "";

export function setTicketId(tid) {
  tickID = tid;
}


export default class TicketProcessing extends Component {


    static navigationOptions = {
        title: 'Ticket Processing',
        headerStyle: {
          backgroundColor: '#8eacbb'
        },
        headerTitleStyle: {
          color: '#FFF'
        }
      }

    constructor() {
		super();

		this.state = {
            ticketOutcome: "",
            observations: ""
		}
    }


    async onSubmitPressed() {
      console.log(tickID)
      alert("Observations submitted")
      fetch(URL + '/projects/' + key + '/tickets/' + tickID + '/observations' ,{
             method: 'POST',
             headers: getAuthForPost(),
             body:  JSON.stringify({outcome: this.state.ticketOutcome, quantity: this.state.observations})
         })
       
     
       //on submit pressed return back to ticket overview
   }
    
    render() {
        return (
            <View style={styles.container}>
					<Picker
						style = {{width: 200, borderColor: 'gray', borderWidth: 1,}}
                        selectedValue = {this.state.ticketOutcome}
                        onValueChange = {(text) => this.setState({ticketOutcome: text})}
						
					>
						<Picker.Item label = "POSITIVE" value = "POSITIVE" />
						<Picker.Item label = "NEGATIVE" value = "NEGATIVE" />
					</Picker>
					<TextInput
                        placeholder = "Observations"
                        placeholderTextColor="#FFF" 
                        underlineColorAndroid="transparent"
						style = {styles.input}
						onChangeText = {(text) => this.setState({observations: text})}
						value = {this.state.observations}
					/>
                    <TouchableOpacity
                      onPress={this.onSubmitPressed.bind(this)}
                        style={styles.buttonContainer}>
                     <Text 
                        style={styles.buttonText}>Submit</Text>
                     </TouchableOpacity>
            </View>
        );

    }
}