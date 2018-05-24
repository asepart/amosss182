import React, {Component} from 'react';
import {Button,ActivityIndicator,View} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class DeleteUserConfirm extends Component {

	constructor(props) {
		super(props);
		this.state = {
			loginName: this.props.id,
			entryKey: this.props.project,
			firstName: this.props.firstName,
			lastName: this.props.lastName
		};
	}

  async deleteUser() {
    var url = URL;
    if (this.state.entryKey !== undefined) {
    		url += '/projects/' + this.state.entryKey
    }
    url += '/users/' + this.state.loginName;
    await fetch(url, {method:'DELETE', headers: getAuth()})
      .then((response) => response.json())
      .catch((error) => {
        console.error(error);
      });
    this.showUserList();
  }

  showUserList () {
    setState({
      isAuth: true,
      show: 'listUsers',
      param: this.props.project,
			name: this.props.name
    });
  }

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		var projectName = '';
		if (this.props.name !== undefined) {
			projectName = " from " + this.props.name;
		}
		return (
			<View>
			<Button
				disabled = {true}
				title = {"Delete " + this.state.firstName + " " + this.state.lastName + projectName + "?"}
			/>
			<Button onPress = { this.deleteUser.bind(this) } title = "Delete" color = "#0c3868"/>
			<Button onPress = { this.showUserList.bind(this) } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
