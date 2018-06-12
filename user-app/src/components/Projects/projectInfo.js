import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ActivityIndicator, FlatList} from 'react-native';
import styles from '../Login/Design';
import { StackNavigator, } from 'react-navigation';
import {getAuth,username,psw} from '../Login/auth';
import {URL} from '../Login/const';
import {key} from './keyValid';

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
		return (
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
				{this._getTicketStatus({item})}
				<Text style={styles.buttonText}>
					accepted: {item.U}
				</Text>
				<Text style={styles.buttonText}>
					positive: {item.UP}
				</Text>
			</TouchableOpacity>
		);
	}

	_getTicketStatus({item}) {
		ticketstatus = item.status;
		switch (ticketstatus){
			case 'open':
				return (
					<Text style={styles.buttonText}>
						status: {item.status}
				 	</Text> );
			case 'accepted':
				return (
					<Text style={styles.buttonTextAccepted}>
						status: {item.status}
				 	</Text> );
			case 'in progress':
				return (
					<Text style={styles.buttonInProgress}>
						status: {item.status}
					</Text> );
			case 'processed':
				return (
					<Text style={styles.buttonCompleted}>
						status: {item.status}
					</Text> );
			case 'finished':
				return (
					<Text style={styles.buttonCompleted}>
						status: {item.status}
					</Text> );
			default:
				return(
					<Text style={styles.buttonText}>
						status: {item.status}
					</Text> );
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
			</View>
		);
	}
}
