import React, { Component } from 'react';
import { View, Button } from 'react-native';
import Popup from "reactjs-popup";
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class DeleteProjectButton extends Component {

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

	deleteProject() {
		var url = URL;
		url += '/projects/' + this.props.proj.row.entryKey;
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
		return (
			<View>
				<Link to = "/" style={{textDecoration: 'none'}}>
					<img onClick={this.openPopup} style={{height: 25, marginBottom: -5}} src={require('../images/delete.png')} alt=""/>
				</Link>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
					<View>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {"Delete " + this.props.proj.row.name + "?"}
						/>
						<Button onPress = { this.deleteProject.bind(this) } title = "Delete" color = "#0c3868"/>
						<Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
					</View>
				</Popup>
			</View>
		);
	}
}
