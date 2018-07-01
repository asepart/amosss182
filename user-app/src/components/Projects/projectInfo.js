import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ActivityIndicator, FlatList} from 'react-native';
import styles from '../Login/Design';
import { StackNavigator, } from 'react-navigation';
import {getAuth,username,psw} from '../Login/auth';
import {URL} from '../Login/const';
import {key} from './keyValid';
import {getUpdateBoolean, setUpdateBoolean} from '../Login/state';

window.btoa = require('Base64').btoa;
export var ticketstatus = '';

export default class ProjectInfo extends Component {

	constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
				tickets: [],
		};
	}

	componentDidMount() {
			this.fetchTicketDetails();
	}

	componentDidUpdate() {
		if(getUpdateBoolean() === true) {
			this.fetchTicketDetails();
			setUpdateBoolean(false);
		}
		}

		componentWillUnmount() {
		const { navigate } = this.props.navigation;
		navigate("Tenth", { name: "ProjectList" })
		setUpdateBoolean(true);
	}


	fetchTicketDetails() {
		fetch(URL + '/projects/' + key + '/tickets', {method:'GET', headers: getAuth()})
				.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							isLoading: false,
							tickets: responseJson
						}, function() {});
					}).catch((error) => {
						console.error(error);
					});
	}

	leaveProject() {
		fetch(URL + '/leave', {
			method: 'POST',
			headers: getAuth(),
			body:	key
		})
		const { navigate } = this.props.navigation;
		navigate("Tenth", { name: "ProjectList" })
	}

	static navigationOptions= {
		title: 'Tickets',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
	}

	_renderItem({item}) {
		return (this._getTicketStatus({item}));
	}

	_getTicketStatus({item}) {
		ticketstatus = item.status;
		switch (ticketstatus){
			case 'open':
				return (<TouchableOpacity
					onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
					style={styles.buttonLargeContainer}
			>
					<Text style={styles.buttonText}>
						id:	{item.id}
					</Text>
					<Text style={styles.buttonText}>
						 summary: {item.summary}
					</Text>
				 <Text style={styles.buttonText}>
						category: {item.category}
				 </Text>
					<Text style={styles.buttonText}>
						 status: {item.status}
				 </Text>
				<Text style={styles.buttonText}>
						 accepted: {item.U}
				 </Text>
				 <Text style={styles.buttonText}>
						 positive: {item.UP}
				 </Text>
				</TouchableOpacity> );
			case 'accepted':
				return (	<TouchableOpacity
					onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
					style={styles.buttonAcceptedContainer}
			>
					<Text style={styles.buttonText}>
						id:	{item.id}
					</Text>
					<Text style={styles.buttonText}>
						 summary: {item.summary}
					</Text>
				 <Text style={styles.buttonText}>
						category: {item.category}
				 </Text>
					<Text style={styles.buttonText}>
						 status: {item.status}
				 </Text>
				<Text style={styles.buttonText}>
						 accepted: {item.U}
				 </Text>
				 <Text style={styles.buttonText}>
						 positive: {item.UP}
				 </Text>
				</TouchableOpacity> );
			case 'processed':
				return (	<TouchableOpacity
					onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
					style={styles.buttonProcessedContainer}
			>
					<Text style={styles.buttonText}>
						id:	{item.id}
					</Text>
					<Text style={styles.buttonText}>
						 summary: {item.summary}
					</Text>
				 <Text style={styles.buttonText}>
						category: {item.category}
				 </Text>
					<Text style={styles.buttonText}>
						 status: {item.status}
				 </Text>
				<Text style={styles.buttonText}>
						 accepted: {item.U}
				 </Text>
				 <Text style={styles.buttonText}>
						 positive: {item.UP}
				 </Text>
				</TouchableOpacity> );
			case 'finished':
				return (<TouchableOpacity
					onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
					style={styles.buttonFinishedContainer}
			>
					<Text style={styles.buttonText}>
						id:	{item.id}
					</Text>
					<Text style={styles.buttonText}>
						 summary: {item.summary}
					</Text>
				 <Text style={styles.buttonText}>
						category: {item.category}
				 </Text>
					<Text style={styles.buttonText}>
						 status: {item.status}
				 </Text>
				<Text style={styles.buttonText}>
						 accepted: {item.U}
				 </Text>
				 <Text style={styles.buttonText}>
						 positive: {item.UP}
				 </Text>
				</TouchableOpacity> );
			default:
				return(
					<TouchableOpacity
					onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
					style={styles.buttonLargeContainer}
			>
					<Text style={styles.buttonText}>
						id:	{item.id}
					</Text>
					<Text style={styles.buttonText}>
						 summary: {item.summary}
					</Text>
				 <Text style={styles.buttonText}>
						category: {item.category}
				 </Text>
					<Text style={styles.buttonText}>
						 status: {item.status}
				 </Text>
				<Text style={styles.buttonText}>
						 accepted: {item.U}
				 </Text>
				 <Text style={styles.buttonText}>
						 positive: {item.UP}
				 </Text>
				</TouchableOpacity>
				);
		}
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		return (
			<View style={styles.container}>
				<FlatList
					style={styles.textLarge}
					data={this.state.tickets}
					renderItem={this._renderItem.bind(this)}
					 keyExtractor={(item, index) => index}
				/>

				<TouchableOpacity
					onPress={this.leaveProject.bind(this)}
					style={styles.buttonContainer}>
					<Text style={styles.buttonText}>Leave Project</Text>
				</TouchableOpacity>
			</View>
		);
	}
}

