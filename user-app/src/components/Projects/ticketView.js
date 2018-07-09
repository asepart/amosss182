import React, { Component } from 'react';
import { StyleSheet, ScrollView, ActivityIndicator, Text, View, TouchableOpacity, Button,FlatList,Image, Linking } from 'react-native';
import { ticket } from '../Chat/sendMessages';
import { key } from './keyValid';
import { URL } from '../Login/const';
import { getAuth, username } from '../Login/auth';
import styles from '../Login/Design';
import { setState } from '../Login/state';
import { setTicketID } from '../Chat/sendMessages';
import { setTicketId } from '../Tickets/ticketProcessed';
import { StackNavigator } from 'react-navigation'
import {status} from '../Projects/projectListTicketList';
import {setUpdateBoolean, getUpdateBoolean} from '../Login/state';
import Icon from 'react-native-vector-icons/FontAwesome';

export var attachmentID = '';
export var attachmentName = '';

export default class TicketView extends Component {

	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state;
		return {
		title: 'Ticket Details',
		headerStyle: {
			backgroundColor: '#8eacbb',
			paddingRight: 15
		},
		headerTitleStyle: {
			color: '#FFF'
		},
		headerRight: <Icon name="user" size={30} color="#FFF"  onPress={ () => params.update() } />
		}
	}

	constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
			isAccepted:"", 
			ticketDetail: "",
			idTicket: "",
			ticketMedia: [],
		};
	}
	
	updateUser = () => {
    	const { navigate } = this.props.navigation;
    	navigate("Thirteenth", { name: "UserInfo" });
    }

	onChatPressed() {
		setTicketID(this.state.idTicket);
		const { navigate } = this.props.navigation;
		navigate("Seventh", { name: "GetMessages" })
	}

	onAcceptPressed() {
		//alert("Ticket successfully accepted")
		let ticketID = this.props.navigation.state.params.id;
		var response = fetch(URL + '/tickets/'+ ticketID + '/accept', {
			method: 'POST',
			headers: getAuth()
		})
		setUpdateBoolean(true);
		this.setState({isAccepted: 'accepted'})
		this.forceUpdate(this.getTicketInfo);
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
		this.props.navigation.setParams({ update: this.updateUser });
		this.setState({isAccepted: status})
		this.getTicketInfo();
		this.fetchTicketMedia();
	}
	
	componentDidUpdate() {
    	if(getUpdateBoolean() === true) {
    		this.getTicketInfo();
    		setUpdateBoolean(false);
    	}
    }

	async fetchTicketMedia() {
		let ticketID = this.props.navigation.state.params.id;
		fetch(URL + '/tickets/' + ticketID + '/attachments', {method:'GET', headers: getAuth()})
				.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							isLoading: false,
							ticketMedia: responseJson
						}, function() {});
					}).catch((error) => {
						console.error(error);
					});
	}

	_renderMedia({item}){
		attachmentID = item.attachmentId;
		attachmentName = item.originalName;
		try { 
			return (
				<TouchableOpacity style={{width:100, height:100}} onPress={ ()=>{ Linking.openURL(URL + '/files/' + attachmentID + '?thumbnail=false')}}>
				<Image  style={{width: 50, height: 50}} source={{uri:URL + '/files/' + attachmentID + '?thumbnail=true'}} />
				<Text style={styles.buttonText}>
				{attachmentName}
				</Text>
				</TouchableOpacity>
				
			)
		} catch(error)  {
			console.error(error);
		}
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
			<View style={styles.containerButtonRow}>
				{this.state.isAccepted === 'open' ? (
				<TouchableOpacity
				onPress={this.onAcceptPressed.bind(this)}
				style={styles.buttonRowContainer}>
				<Text style={styles.buttonText}>Accept</Text>
				</TouchableOpacity>
				) : (
				<TouchableOpacity
				onPress={this.onProcessTicketPressed.bind(this)}
				style={styles.buttonRowContainer}>
				<Text style={styles.buttonText}>Process Ticket</Text>
				</TouchableOpacity>
				)}
					<TouchableOpacity
					onPress={this.onChatPressed.bind(this)}
					style={styles.buttonRowContainer}>
					<Text style={styles.buttonText}>Chat</Text>
				</TouchableOpacity>
			</View>	
			<ScrollView style={styles.containerScroll}>
			
				<Text style={styles.text}>
					ID: {this.state.ticketDetail.id}
				</Text>
				<Text>
				</Text>	
				<Text style={styles.text}>
					Key: {this.state.ticketDetail.projectKey}
				</Text>
				<Text>
				</Text>
				<Text style={styles.text}>
					Ticket Name: {this.state.ticketDetail.name}
				</Text>
				<Text style={styles.text}>
					Summary: {this.state.ticketDetail.summary}
				</Text>
				<Text>
				</Text>
				<Text style={styles.text}>
					Required Observations: {this.state.ticketDetail.requiredObservations}
				</Text>
				<Text>
				</Text>
				<Text style={styles.text}>
					Category: {this.state.ticketDetail.category}
				</Text>
				<Text>
				</Text>
				<Text style={styles.text}>
					Ticket Status: {this.state.ticketDetail.status}
				</Text>
				<Text>
				</Text>
					<Text style={styles.textBold}>
						Description:
					</Text>
					<Text style={styles.text} >
						{this.state.ticketDetail.description}
					</Text>
					<Text>
				</Text>
					<FlatList
					data={this.state.ticketMedia}
					renderItem={this._renderMedia.bind(this)}
					keyExtractor={(item, index) => {return index.toString()}}
					/>	
				</ScrollView>
				
			</View>
		);
	}
}

