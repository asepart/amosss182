import React, { Component } from 'react';
import { View, Button, TextInput } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';

export default class UpdateProjectButton extends Component {

	constructor(props) {
    super(props);
    this.state = {
			open: false,
			loginName: '',
			password: '',
			firstName: '',
			lastName: '',
			phone: '',
		};
  }
  openPopup = () => {
    this.setState({ open: true });
		this.getVars();
  };
  closePopup = () => {
    this.setState({ open: false });
  };

	//needed to get right row values after changes in parent component
	getVars() {
		this.setState({
			loginName: this.props.proj.row.loginName,
			password: this.props.proj.row.password,
			firstName: this.props.proj.row.firstName,
			lastName: this.props.proj.row.lastName,
			phone: this.props.proj.row.phone,
		})
	}

	addUser(){
		let auth = getAuthForPost();
		fetch(URL + '/users', {
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
		this.setState({
		 	open: false
		})
	}

	render() {
		var buttonEnabled = (this.state.loginName !== '' && this.state.password !== '' && this.state.firstName !== '' && this.state.lastName !== '' && this.state.phone !== '');

		return(
			<div>
				<button onClick={this.openPopup} style={{color: '#5daedb'}}>
					EDIT
				</button>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
				<View>
					<TextInput
						placeholder = "Login Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({loginName: text})}
						value = {this.state.loginName}
						editable = { false }
					/>
					<TextInput
						placeholder = "Password"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({password: text})}
						value = {this.state.password}
					/>
					<TextInput
						placeholder = "Given Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({firstName: text})}
						value = {this.state.firstName}
					/>
					<TextInput
						placeholder = "Surname"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({lastName: text})}
						value = {this.state.lastName}
					/>
					<TextInput
						placeholder = "Phone Number"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({phone: text})}
						value = {this.state.phone}
					/>
					<Button onPress = { this.addUser.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
