import React, { Component } from 'react';
import { View, Button, TextInput } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import {setUpdateBoolean} from '../shared/GlobalState';

export default class UpdateUserButton extends Component {

	constructor(props) {
    super(props);
    this.state = {
			open: false,
			loginName: '',
			password: '',
			firstName: '',
			lastName: '',
			phoneNumber: '',
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
			phoneNumber: this.props.proj.row.phoneNumber,
		})
	}

	addUser(){
		let auth = getAuthForPost();
		fetch(URL + '/users', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({loginName: this.state.loginName, password: this.state.password, firstName: this.state.firstName, lastName: this.state.lastName, phoneNumber: this.state.phoneNumber})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({}, function() {});
			})
			.catch((error) => {
				console.error(error);
			});

		this.props.callToParent();
		setUpdateBoolean(true);
		this.setState({
		 	open: false
		})
	}

	render() {
		var buttonEnabled = (this.state.loginName !== '' && this.state.password !== '' && this.state.firstName !== '' && this.state.lastName !== '' && this.state.phoneNumber !== '');

		return(
			<div>
				<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/edit.png')} alt=""/>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
				<View>
					<TextInput
						placeholder = "Login Name"
						style = {{height: 40, borderColor: 'gray', backgroundColor: 'lightgrey',borderWidth: 1, textAlign: 'center'}}
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
						onChangeText = {(text) => this.setState({phoneNumber: text})}
						value = {this.state.phoneNumber}
					/>
					<Button onPress = { this.addUser.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
