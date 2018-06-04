import React, { Component } from 'react';
import { View, Button, TextInput } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost, username} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';

export default class UpdateProjectButton extends Component {

	constructor(props) {
    super(props);
    this.state = {
			open: false,
			projectName: this.props.name,
			entryKey: this.props.project,
			owner: username
		};
  }
  openPopup = () => {
    this.setState({ open: true });
  };
  closePopup = () => {
    this.setState({ open: false });
  };

	putProject() {
		let auth = getAuthForPost();
		fetch(URL + '/projects', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({projectName: this.state.projectName, entryKey: this.state.entryKey, owner: this.state.owner})
			})
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					projectName: "",
					entryKey: "",
					owner: ""
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
		var buttonEnabled = (this.state.entryKey !== '' && this.state.projectName !== '');

		return (	// TODO: add edit icon instead of text here
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
						placeholder = "Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({projectName: text})}
						value = { this.state.projectName }
					/>
					<TextInput
						placeholder = "Entry Code"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = { (text) => this.setState({entryKey: text})}
						value = { this.state.entryKey }
						editable = { false }
					/>
					<Button onPress = { this.putProject.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
					</View>
				</Popup>
			</div>
		);
	}
}
