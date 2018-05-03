import React, { Component } from 'react';
import { Button } from 'react-native';
import { setState } from '../shared/GlobalState';

export default class ProjectButton extends Component {
	displayProject() {
		setState({
			isAuth: true,
			show: 'project',
			param: this
		});
	}
	render() {
		return (
			<Button
			onPress = { this.displayProject.bind(this.props.proj) }
			title = { this.props.proj }
			color = "#841584" />
		);
	}
}
