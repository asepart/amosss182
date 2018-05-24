import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class UserAdd extends Component {

	constructor(props) {
		super(props);
		this.state = {
			loginName: 'UselessEmployee',
			password: 'uselessPassword',
			firstName: 'Useless',
			lastName: 'Employee',
			phone:	'+49123456789'
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
			param: ''
		});
	}

	async addUser(){
		let auth = getAuthForPost();
		await fetch(URL + '/users', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({loginName: this.state.loginName, password: this.state.password, firstName: this.state.firstName, lastName: this.state.lastName, phone: this.state.phone})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					loginName: "",
					password: "",
					firstName: "",
					lastName: "",
					phone: ""
				}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});
		this.showUserList ();
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
					onChangeText = {(text) => this.setState({loginName: text})}
					value = {this.state.loginName}
				/>
				<TextInput
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({password: text})}
					value = {this.state.password}
				/>
				<TextInput
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({firstName: text})}
					value = {this.state.firstName}
				/>
				<TextInput
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({lastName: text})}
					value = {this.state.lastName}
				/>
				<TextInput
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({phone: text})}
					value = {this.state.phone}
				/>
				<Button onPress = { this.addUser.bind(this) } title = "Add" color = "#0c3868" />
				<Button onPress = { this.showUserList.bind() } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
