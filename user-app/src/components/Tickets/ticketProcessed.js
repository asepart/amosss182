import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, Picker, Platform, Button} from 'react-native';
import styles from '../Login/Design';
import { StackNavigator } from 'react-navigation';
import {URL} from '../Login/const';
import {ticket, setTicketID} from '../Chat/sendMessages';
import {key} from '../Projects/keyValid';
import {getAuthForPost, username} from '../Login/auth';
import {setUpdateBoolean} from '../Login/state';

var pickerPlaceholder = "Outcome";
export var tickID = "";
export function setTicketId(tid) {
	tickID = tid;
}

export default class TicketProcessing extends Component {

	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state;
		return {
		title: 'Ticket Processing',
		headerStyle: {
			backgroundColor: '#5daedb'
		},
		headerTitleStyle: {
			color: '#FFF'
		},
		headerRight: <Button title={username} onPress={ () => params.update() } />
		}
	}

	constructor(props) {
		super(props);
		this.state = {
			outcome: pickerPlaceholder,
			quantity: '',
		}
	}
	
	componentDidMount() {
		this.props.navigation.setParams({ update: this.updateUser });
	}
	
	updateUser = () => {
    	const { navigate } = this.props.navigation;
    	navigate("Thirteenth", { name: "UserInfo" });
    }

	onSubmitPressed() {
		console.log(tickID)
		let auth = getAuthForPost();
		alert("Observations submitted")
		fetch(URL + '/tickets/' + tickID + '/observations', {
			method: 'POST',
			headers: auth,
			body:	JSON.stringify({outcome: this.state.outcome, quantity: this.state.quantity})
		}).then( (response) => {
			console.log(response.status);
		});

		setUpdateBoolean(true);
		
		//on submit pressed return back to ticket overview
		const { navigate } = this.props.navigation;
		navigate("Sixth", { name: "TicketView" })
	 }

	render() {
		var buttonEnabled = (this.state.outcome !== pickerPlaceholder && this.state.quantity !== '');
		var pickerStyle = styles.inputPicker;
		if (Platform.OS === 'ios') {
			pickerStyle = styles.inputPickerIOS;
		}
		return (
			<View style={styles.containerPicker}>
				<Picker
					style = {pickerStyle}
				itemStyle={{color: '#FFF'}}	
				selectedValue = {this.state.outcome}
					onValueChange = {(text) => this.setState({outcome: text})}
				>
					<Picker.Item label = { pickerPlaceholder} value = { pickerPlaceholder}/>
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
					disabled={!buttonEnabled}
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
