import React, {Component} from 'react';
import {Button,ActivityIndicator,View,Text} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';
import { Link, Redirect } from 'react-router-dom';

export default class DeleteProjectConfirm extends Component {

	constructor(props) {
		super(props);
		this.state = {
			projectName: this.props.name,
			entryKey: this.props.project,
		};
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: '',
			param: ''
		});
	}

  async deleteProject() {
    var url = URL;
		url += '/projects/' + this.state.entryKey;
    await fetch(url, {method:'DELETE', headers: getAuth()})
      .then((response) => response.json())
      .catch((error) => {
        console.error(error);
      });
    this.showProjectList ();
		this.setState({
			redirect: true
		  })
	}
	
	renderRedirect = () => {
		if (this.state.redirect) {
		  return <Redirect to="/"/>
		}
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator />
				</View>
			)
		}
		return (	// TODO: add home icon instead of text here
			<View>
			<Button
				onPress = { function doNothing() {} }
				disabled = {true}
				title = {"Delete " + this.props.name + "?"}
			/>
			{this.renderRedirect()}
			<Button onPress = { this.deleteProject.bind(this) } title = "Delete" color = "#0c3868"/>
			<Link to="/" style={{textDecoration: 'none'}}>
				<Button onPress = { this.showProjectList } title = "Cancel" color = "#0e4a80" />
			</Link>
			</View>
		);
	}
}
