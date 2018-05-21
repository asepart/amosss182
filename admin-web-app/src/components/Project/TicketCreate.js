import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class TicketCreate extends Component {
	constructor(props) {
		super(props);
		this.state = {
			ticketName: "Useless Name",
			ticketSummary: "Useless Summary",
			ticketDescription: "Useless Description",
			ticketCategory: "ONE_TIME_ERROR",
			requiredObservations: "5"
		};
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: '',
			param: ''
		});
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
			name: this.props.name
		});
	}

	async createTicket() {
		let auth = getAuthForPost();
		await fetch(URL + '/projects/' + this.props.project + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({ticketName: this.state.ticketName, ticketSummary: this.state.ticketSummary, ticketDescription: this.state.ticketDescription, ticketCategory: this.state.ticketCategory, requiredObservations: this.state.requiredObservations})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					ticketName: "",
					ticketSummary: "",
					ticketDescription: "",
					ticketCategory: "",
					requiredObservations: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showTicketList ();
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		return(
				<View>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketName: text})}
						value = {this.state.ticketName}
					/>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketSummary: text})}
						value = {this.state.ticketSummary}
					/>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketDescription: text})}
						value = {this.state.ticketDescription}
					/>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({ticketCategory: text})}
						value = {this.state.ticketCategory}
					/>
					<TextInput
						style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({requiredObservations: text})}
						value = {this.state.requiredObservations}
					/>
					<Button onPress = { this.createTicket.bind(this) } title = "Create" color = "#0c3868" />
					<Button onPress = { this.showTicketList.bind(this) } title = "Cancel" color = "#0e4a80" />
				</View>
		);
	}
}
