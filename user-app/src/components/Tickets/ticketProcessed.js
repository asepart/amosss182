import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Picker} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {URL} from '../Login/const';
import {key} from './keyValid';

var pickerPlaceholder = "Outcome";

export default class TicketProcessing extends Component {

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
						style = {{height: 40, backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1, textAlign: 'center'}}
						onValueChange = {(text) => this.setState({ticketOutcome: text})}
						selectedValue = {this.state.ticketOutcome}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "Positive" value = "Positive" />
						<Picker.Item label = "Negative" value = "Negative" />
					</Picker>
					<TextInput
						placeholder = "Observations"
						style = {styles.input}
						onChangeText = {(text) => this.setState({observations: text})}
						value = {this.state.observations}
					/>
            </View>
        );

    }
}