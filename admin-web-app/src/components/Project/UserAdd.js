import React, {Component} from 'react';
import {Button,TextInput,ActivityIndicator,View} from 'react-native';
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

var button = "Add";
var editKey = true;

export default class UserAdd extends Component {

	constructor(props) {
		super(props);
		this.state = {
			loginName: this.props.id,
			password: this.props.password,
			firstName: this.props.firstName,
			lastName: this.props.lastName,
			phone: this.props.phone
		};
		if(this.state.loginName !== '') {
			button = "Update";
			editKey = false;
		} else {
			button = "Add";
			editKey = true;
		}
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
		var buttonEnabled = (this.state.loginName !== '' && this.state.password !== '' && this.state.firstName !== '' && this.state.lastName !== '' && this.state.phone !== '');
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
					placeholder = "Login Name"
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({loginName: text})}
					value = {this.state.loginName}
					editable = {editKey}
				/>
				<TextInput
					placeholder = "Password"
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({password: text})}
					value = {this.state.password}
				/>
				<TextInput
					placeholder = "Given Name"
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({firstName: text})}
					value = {this.state.firstName}
				/>
				<TextInput
					placeholder = "Surname"
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({lastName: text})}
					value = {this.state.lastName}
				/>
				<TextInput
					placeholder = "Phone Number"
					style = {{height: 40, width: '25em', borderColor: 'gray',borderWidth: 1}}
					onChangeText = {(text) => this.setState({phone: text})}
					value = {this.state.phone}
				/>
				<Button onPress = { this.addUser.bind(this) } title = {button} color = "#0c3868" disabled = {!buttonEnabled}/>
				<Button onPress = { this.showUserList.bind() } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
