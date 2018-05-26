import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View,Picker,Text} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

var button = "Add";
var pickerPlaceholder = "Category";

export default class TicketCreate extends Component {
	constructor(props) {
		super(props);
		this.state = {
			ticketName: this.props.tName,
			ticketSummary: this.props.tSummary,
			ticketDescription: this.props.tDescription,
			ticketCategory: this.props.tCategory,
			requiredObservations: this.props.tRequiredObservations,
			id: this.props.tId
		};
		if(this.state.id !== '0') {
			button = "Update";
		} else {
			button = "Add";
		}
	}

	showUserList () {
		setState({
			isAuth: true,
			show: 'listUsers',
			param: this.props.project,
			name: this.props.name
		});
	}
	
	showTicketList () {
		setState({
			isAuth: true,
			show: 'showTickets',
			param: this.props.project,
			name: this.props.name,
			tName: '',
			tSummary: '',
			tDescription: '',
			tCategory: pickerPlaceholder,
			tRequiredObservations: '',
			tId: '0'
		});
	}
	
	showProjectList () {
		setState({
			isAuth: true,
			show: ''
		});
	}

	async createTicket() {
		let auth = getAuthForPost();
		await fetch(URL + '/projects/' + this.props.project + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({id: this.state.id, ticketName: this.state.ticketName, ticketSummary: this.state.ticketSummary, ticketDescription: this.state.ticketDescription, ticketCategory: this.state.ticketCategory, requiredObservations: this.state.requiredObservations})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					ticketName: "",
					ticketSummary: "",
					ticketDescription: "",
					ticketCategory: "",
					requiredObservations: "",
					id: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showTicketList ();
	}

	render() {console.log(this.state.ticketName);
		var buttonEnabled = (this.state.ticketName !== '' && this.state.ticketSummary !== '' && this.state.ticketDescription !== '' && this.state.ticketCategory !== pickerPlaceholder && this.state.requiredObservations !== '');
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		return(	// TODO: add home icon instead of text here
				<View>
					<Text
						onPress = { this.showProjectList.bind(this) }
						style={{color: '#5daedb'}}
					>
						HOME
					</Text>
					<TextInput
						placeholder = "Name"
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketName: text})}
						value = {this.state.ticketName}
					/>
					<TextInput
						placeholder = "Summary"
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketSummary: text})}
						value = {this.state.ticketSummary}
					/>
					<TextInput
						placeholder = "Description"
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketDescription: text})}
						value = {this.state.ticketDescription}
					/>
					<Picker
						style = {{height: 40, width: '22em', backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1}}
						onValueChange = {(text) => this.setState({ticketCategory: text})}
						selectedValue = {this.state.ticketCategory}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "ONE_TIME_ERROR" value = "ONE_TIME_ERROR" />
						<Picker.Item label = "TRACE" value = "TRACE" />
						<Picker.Item label = "BEHAVIOR" value = "BEHAVIOR" />
					</Picker>
					<TextInput
						placeholder = "Required Observations"
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({requiredObservations: text})}
						value = {this.state.requiredObservations}
					/>
					<Button onPress = { this.createTicket.bind(this) } title = {button} color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.showTicketList.bind(this) } title = "Cancel" color = "#0e4a80" />
				</View>
		);
	}
}
