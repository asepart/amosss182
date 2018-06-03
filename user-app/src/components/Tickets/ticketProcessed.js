import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Picker} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {URL} from '../Login/const';

var pickerPlaceholder = "Outcome";

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
    
    render() {
        return (
            <View style={styles.container}>
					<Picker
						style = {{height: 40, backgroundColor: '#8eacbb', borderColor: 'gray', borderWidth: 1, textAlign: 'center'}}
						onValueChange = {(text) => this.setState({ticketOutcome: text})}
						selectedValue = {this.state.ticketOutcome}
					>
						<Picker.Item label = {pickerPlaceholder}  />
						<Picker.Item label = "Positive" value = "Positive" />
						<Picker.Item label = "Negative" value = "Negative" />
					</Picker>
					<TextInput
                        placeholder = "Observations"
                        placeholderTextColor="#FFF" 
                        underlineColorAndroid="transparent"
						style = {styles.input}
						onChangeText = {(text) => this.setState({observations: text})}
						value = {this.state.observations}
					/>
            </View>
        );

    }
}