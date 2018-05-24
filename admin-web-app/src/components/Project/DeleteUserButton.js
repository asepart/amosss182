import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';

export default class DeleteUserButton extends Component {

  showDeleteUserConfirm() {
		setState({
			isAuth: true,
			show: 'deleteUser',
			param: this.props.keyFromParent,
      name: this.props.nameFromParent,
      id: this.props.proj.row.loginName
		});
	}

	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.showDeleteUserConfirm.bind(this) }
				style={{color: '#5daedb'}}
			>
				DELETE
			</Text>
		);
	}
}
