import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

export default class DeleteUserButton extends Component {

  showDeleteUserConfirm() {
		setState({
			isAuth: true,
			show: 'deleteUser',
			param: this.props.keyFromParent,
			name: this.props.nameFromParent,
			id: this.props.proj.row.loginName,
			firstName: this.props.proj.row.firstName,
			lastName: this.props.proj.row.lastName
		});
	}

	render() {
		return (	// TODO: add delete icon instead of text here
			<Text
				onPress = { this.showDeleteUserConfirm.bind(this) }
				style={{color: '#5daedb'}}
			>
				DELETE
			</Text>
		);
	}
}
