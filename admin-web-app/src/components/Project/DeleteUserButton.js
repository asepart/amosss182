import React, { Component } from 'react';
import { View, Button } from 'react-native';
import Popup from "reactjs-popup";
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class DeleteUserButton extends Component {

	constructor(props) {
		super(props);
		this.state = { open: false };
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({ open: false });
	};

	deleteUser() {
		var url = URL;
		if (this.props.keyFromParent !== undefined) {
				url += '/projects/' + this.props.keyFromParent
		}
		url += '/users/' + this.props.proj.row.loginName;
		fetch(url, {method:'DELETE', headers: getAuth()})
			.then((response) => response.json())
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
		var button = "Delete ";
		var projectName = '';
		if (this.props.nameFromParent !== undefined) {
			button = "Remove "
			projectName = " from " + this.props.nameFromParent;
		}

		return (
			<div>
				<div>
					{window.location.href.indexOf("usermanagement") !== -1 ? (
						<Link to = "/usermanagement" style={{textDecoration: 'none'}}>
							<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/delete.png')} alt=""/>
						</Link>
					) : (
						<Link to = {"/projects/" + this.props.project + "/users"} style={{textDecoration: 'none'}}>
							<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/delete.png')} alt=""/>
						</Link>
					)}
				</div>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
					<View>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {button + this.props.proj.row.firstName + " " + this.props.proj.row.lastName + projectName + "?"}
						/>
						<Button onPress = { this.deleteUser.bind(this) } title = {button} color = "#0c3868"/>
						<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
					</View>
				</Popup>
			</div>
		);
	}
}
