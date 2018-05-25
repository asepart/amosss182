import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';

export default class UpdateProjectButton extends Component {
	displayUser() {
		setState({
			isAuth: true,
			show: 'addUser',
			id: this.props.proj.row.loginName,
			password: this.props.proj.row.password,
			firstName: this.props.proj.row.firstName,
			lastName: this.props.proj.row.lastName,
			phone: this.props.proj.row.phone
		});
	}
	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.displayUser.bind(this) }
				style={{color: '#5daedb'}}
			>
				EDIT
			</Text> 
		);
	}
}
