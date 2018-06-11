import React, { Component } from 'react';
import { View, Button, TextInput } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost, username} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import {setUpdateBoolean} from '../shared/GlobalState';

export default class UpdateProjectButton extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			projectName: '',
			entryKey: '',
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
			projectName: this.props.proj.row.projectName,
			entryKey: this.props.proj.row.entryKey,
		})
	}

	putProject() {
		let auth = getAuthForPost();
		fetch(URL + '/projects', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({projectName: this.state.projectName, entryKey: this.state.entryKey, owner: username})
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
		var buttonEnabled = (this.state.entryKey !== '' && this.state.projectName !== '');

		return (
			<div>
				<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/edit.png')} alt=""/>
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
						style = {{height: 40, borderColor: 'gray', backgroundColor: 'lightgrey', borderWidth: 1, textAlign: 'center'}}
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
