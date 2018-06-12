import React, { Component } from 'react';
import { StyleSheet, ScrollView, ActivityIndicator, Text, View, TouchableOpacity } from 'react-native';
import { ticket } from '../Chat/sendMessages';
import { key } from './keyValid';
import { URL } from '../Login/const';
import { getAuth } from '../Login/auth';
import styles from '../Login/Design';
import { setState } from '../Login/state';
import { setTicketID } from '../Chat/sendMessages';
import { setTicketId } from '../Tickets/ticketProcessed';
import { StackNavigator } from 'react-navigation'
import {ticketstatus} from '../Projects/projectInfo';
import {getUpdateBoolean, setUpdateBoolean} from '../Login/state';

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
		alert("Ticket successfully accepted")
		let ticketID = this.props.navigation.state.params.id;
		var response = fetch(URL + '/tickets/'+ ticketID + '/accept', {
			method: 'POST',
			headers: getAuth()
		})
		setUpdateBoolean(true);
	}

	onProcessTicketPressed() {
		setTicketId(this.state.idTicket);
		const { navigate } = this.props.navigation;
		navigate("Eigth", { name: "TicketProcessing" })
	}

	async getTicketInfo() {
		let ticketID = this.props.navigation.state.params.id;
		this.setState({
			idTicket: ticketID
		})
		fetch(URL + '/tickets/' + ticketID, { method: 'GET', headers: getAuth() })
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
		this.getTicketInfo();
	}

	componentDidUpdate() {
		if(getUpdateBoolean() === true) {
		  this.getTicketInfo();
		  setUpdateBoolean(false);
		}
	  }

	componentWillUnmount() {
		const { navigate } = this.props.navigation;
        navigate("Fourth", { name: "ProjectInfo" })
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

				<View>
				{ticketstatus === 'open' ? (
			<TouchableOpacity
				onPress={this.onAcceptPressed.bind(this)}
				style={styles.buttonContainer}>
				<Text style={styles.buttonText}>Accept</Text>
			</TouchableOpacity>
		) : (
			<TouchableOpacity
				onPress={this.onProcessTicketPressed.bind(this)}
				style={styles.buttonContainer}>
				<Text style={styles.buttonText}>Process Ticket</Text>
			</TouchableOpacity>
		)}
				</View>

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
					Category: {this.state.ticketDetail.category}
				</Text>
				<Text style={styles.text}>
					Ticket Name: {this.state.ticketDetail.name}
				</Text>
				<Text style={styles.text}>
					Ticket Status: {this.state.ticketDetail.status}
				</Text>
				<Text style={styles.text}>
					Summary: {this.state.ticketDetail.summary}
				</Text>
				<ScrollView style={styles.containerScroll}>
					<Text style={styles.textLarge}>
						Description:
					</Text>
					<Text style={styles.text} >
						{this.state.ticketDetail.description}
					</Text>
				</ScrollView>
			</View>
		);
	}
}
