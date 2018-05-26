import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import { Link } from 'react-router-dom';

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
			<Link to="/deleteproject" style={{textDecoration: 'none'}}>
				<Text
					onPress = { this.showDeleteProjectConfirm.bind(this) }
					style={{color: '#5daedb'}}
				>
					DELETE
				</Text>
			</Link>
		);
	}
}
