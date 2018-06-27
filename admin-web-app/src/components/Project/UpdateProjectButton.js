import React, { Component } from 'react';
import { Text, View, Button, TextInput, CheckBox } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost, username} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class UpdateProjectButton extends Component {
	constructor(props) {
		super(props);
		this.state = {
			open: false,
			name: '',
			entryKey: '',
			finished: false
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
			name: this.props.proj.row.name,
			entryKey: this.props.proj.row.entryKey,
			finished: this.props.proj.row.finished,
		})
	}

	putProject() {
		let auth = getAuthForPost();
		fetch(URL + '/projects', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({name: this.state.name, entryKey: this.state.entryKey, owner: username, finished: (this.state.finished ? "true" : "false")})
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
		var buttonEnabled = (this.state.entryKey !== '' && this.state.name !== '');

		return (
			<div>
				<Link to = "/" style={{textDecoration: 'none'}}>
					<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/edit.png')} alt=""/>
				</Link>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
					<View>
					<TextInput
						placeholder = "Name"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({name: text})}
						value = { this.state.name }
					/>
					<TextInput
						placeholder = "Entry Code"
						style = {{height: 40, borderColor: 'gray', backgroundColor: 'lightgrey', borderWidth: 1, textAlign: 'center'}}
						onChangeText = { (text) => this.setState({entryKey: text})}
						value = { this.state.entryKey }
						editable = { false }
					/>
					<View style={{ flexDirection: 'row' }}>
						<CheckBox
							value={this.state.finished}
							onValueChange={() => this.setState({ finished: !this.state.finished })}
						/>
						<Text> Finished?</Text>
					</View>
					<Button onPress = { this.putProject.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
					</View>
				</Popup>
			</div>
		);
	}
}
