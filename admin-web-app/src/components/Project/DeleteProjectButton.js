import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

export default class DeleteProjectButton extends Component {

  showDeleteProjectConfirm() {
		setState({
			isAuth: true,
			show: 'deleteProject',
			param: this.props.proj.row.entryKey,
			name: this.props.proj.row.projectName
		});
	}

	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.showDeleteProjectConfirm.bind(this) }
				style={{color: '#5daedb'}}
			>
				DELETE
			</Text>
		);
	}
}
