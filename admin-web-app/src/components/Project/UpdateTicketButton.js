import React, { Component } from 'react';
import { View, Button, TextInput, Picker } from 'react-native';
import Popup from "reactjs-popup";
import {getAuthForPost} from '../shared/auth';
import {URL} from '../shared/const';
import '../../index.css';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

var pickerPlaceholder = "Category";

export default class UpdateTicketButton extends Component {

	constructor(props) {
		super(props);
		this.state = {
			open: false,
			name: '',
			summary: '',
			description: '',
			category: '',
			requiredObservations: '',
			id: '',
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
			name: this.props.tick.row.name,
			summary: this.props.tick.row.summary,
			description: this.props.tick.row.description,
			category: this.props.tick.row.category,
			requiredObservations: this.props.tick.row.requiredObservations,
			id: this.props.tick.row.id
		})
	}

	createTicket() {
		let auth = getAuthForPost();
		fetch(URL + '/tickets/', {
				method: 'POST',
				headers: auth,
				body: JSON.stringify({id: this.state.id, name: this.state.name, summary: this.state.summary, description: this.state.description, category: this.state.category, requiredObservations: this.state.requiredObservations, projectKey: this.props.project})
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
		var buttonEnabled = (this.state.name !== '' && this.state.summary !== '' && this.state.description !== '' && this.state.category !== pickerPlaceholder && this.state.requiredObservations !== '');

		return (
			<div>
				<Link to = {"/projects/" + this.props.project} style={{textDecoration: 'none'}}>
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
						value = {this.state.name}
					/>
					<TextInput
						placeholder = "Summary"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({summary: text})}
						value = {this.state.summary}
					/>
					<TextInput
						placeholder = "Description"
						multiline={true}
						style = {{height: window.innerHeight*0.4, borderColor: 'gray',borderWidth: 1}}
						onChangeText = {(text) => this.setState({description: text})}
						value = {this.state.description}
					/>
					<Picker
						style = {{height: 40, backgroundColor: 'transparent', borderColor: 'gray', borderWidth: 1, textAlign: 'center'}}
						onValueChange = {(text) => this.setState({category: text})}
						selectedValue = {this.state.category}
					>
						<Picker.Item label = {pickerPlaceholder} value = {pickerPlaceholder} />
						<Picker.Item label = "one-time-error" value = "one-time-error" />
						<Picker.Item label = "trace" value = "trace" />
						<Picker.Item label = "behavior" value = "behavior" />
					</Picker>
					<TextInput
						placeholder = "Required Observations"
						style = {{height: 40, borderColor: 'gray',borderWidth: 1, textAlign: 'center'}}
						onChangeText = {(text) => this.setState({requiredObservations: text})}
						value = {`${this.state.requiredObservations}`}
					/>
					<Button onPress = { this.createTicket.bind(this) } title = "Update" color = "#0c3868" disabled = {!buttonEnabled}/>
					<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
				</View>
				</Popup>
			</div>
		);
	}
}
