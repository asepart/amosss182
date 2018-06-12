import React, {Component} from 'react';
import {Button,TextInput,View} from 'react-native';
import {getAuthForPost, username} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import Popup from "reactjs-popup";
import {setUpdateBoolean} from '../shared/GlobalState';

export default class ProjectAdd extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			name: this.props.name,
			entryKey: this.props.project,
			owner: username
		};
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({
				open: false,
				name: undefined,
				entryKey: undefined,
		})
	};

	putProject() {
		let auth = getAuthForPost();
		fetch(URL + '/projects', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({name: this.state.name, entryKey: this.state.entryKey, owner: this.state.owner})
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
		this.closePopup();
	}

	render() {
		var buttonEnabled = (this.state.entryKey !== undefined && this.state.name !== undefined && this.state.entryKey !== '' && this.state.name !== '');

		return (
			<div>
				<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/add.png')} alt=""/>
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
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = { (text) => this.setState({entryKey: text})}
						value = { this.state.entryKey }
					/>
					<Button onPress = { this.putProject.bind(this) } title = "Add" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
					</View>
				</Popup>
			</div>
		);
	}
}
