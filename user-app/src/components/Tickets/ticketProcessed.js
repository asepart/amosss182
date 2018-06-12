import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Picker} from 'react-native';
import styles from '../Login/Design';
import { StackNavigator } from 'react-navigation';
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

	constructor(props) {
		super(props);
		this.state = {
			outcome: '',
			quantity: '',
		}
	}

	onSubmitPressed() {
		console.log(tickID)
		let auth = getAuthForPost();
		alert("Observations submitted")
		fetch(URL + '/tickets/' + tickID + '/observations', {
			method: 'POST',
			headers: auth,
			body:	JSON.stringify({outcome: this.state.outcome, quantity: this.state.quantity})
		})

		const { navigate } = this.props.navigation;
		navigate("Fourth", { name: "ProjectInfo" })
		//on submit pressed return back to ticket overview
	 }

	render() {
		return (
			<View style={styles.container}>
				<Picker
					style = {{width: 200, color: '#FFF', borderColor: 'gray', borderWidth: 1,}}
					selectedValue = {this.state.outcome}
					onValueChange = {(text) => this.setState({outcome: text})}
				>
					<Picker.Item label = { pickerPlaceholder} value = { pickerPlaceholder} />
					<Picker.Item label = "POSITIVE" value = "positive" />
					<Picker.Item label = "NEGATIVE" value = "negative" />
				</Picker>
				<TextInput
					placeholder = "Observations"
					placeholderTextColor="#FFF"
					underlineColorAndroid="transparent"
					style = {styles.input}
					onChangeText = {(text) => this.setState({quantity: text})}
					value = {this.state.quantity}
				/>
				<TouchableOpacity
					onPress={this.onSubmitPressed.bind(this)}
					style={styles.buttonContainer}
				>
					<Text style={styles.buttonText}>
						Submit
					</Text>
				</TouchableOpacity>
			</View>
		);
	}
}
