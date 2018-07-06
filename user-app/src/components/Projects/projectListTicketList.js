import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ActivityIndicator, FlatList, Button} from 'react-native';
import styles from '../Login/Design';
import { StackNavigator, } from 'react-navigation';
import {getAuth,username,psw} from '../Login/auth';
import {URL} from '../Login/const';
import {getUpdateBoolean, setUpdateBoolean} from '../Login/state';

window.btoa = require('Base64').btoa;
export var status = '';

export default class ProjectListTicketList extends Component {

	constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
				tickets: [],
		};
	}

	componentDidMount() {
		this.props.navigation.setParams({ update: this.updateUser });
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
        let projectKey = this.props.navigation.state.params.entryKey;
		fetch(URL + '/projects/' + projectKey + '/tickets', {method:'GET', headers: getAuth()})
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
        let projectKey = this.props.navigation.state.params.entryKey;
		fetch(URL + '/leave', {
			method: 'POST',
			headers: getAuth(),
			body:	projectKey
		})
		const { navigate } = this.props.navigation;
		navigate("Tenth", { name: "ProjectList" })
	}

	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state;
		return {
		title: 'Tickets',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		},
		headerRight: <Button title={username} onPress={ () => params.update() } />
		}
	}
	
	updateUser = () => {
    	const { navigate } = this.props.navigation;
    	navigate("Thirteenth", { name: "UserInfo" });
    }

	_renderItem({item}) {
		return (this._getTicketStatus({item}));
	}

	_getTicketStatus({item}) {
		status = item.status;
		switch (status){
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
					data={this.state.tickets}
					renderItem={this._renderItem.bind(this)}
					 keyExtractor={(item, index) => index.toString()}
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