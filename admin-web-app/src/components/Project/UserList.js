import React, { Component } from 'react';
import {ActivityIndicator,Button,View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateUserButton from './UpdateUserButton';
import DeleteUserButton from './DeleteUserButton';
import UserAdd from './UserAdd';

import 'react-table/react-table.css';
import '../../index.css';
import Cookies from 'universal-cookie';
import {Link} from 'react-router-dom'
import {getUpdateBoolean, setUpdateBoolean} from '../shared/GlobalState';

export default class UserList extends Component {

	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
		const cookies = new Cookies();

		console.log(cookies.get('myCat')); // Pacman
	}

	componentDidMount() {
		this.fetchMetaData();
		this.fetchUsers();
	}

	componentDidUpdate() {
		if(getUpdateBoolean() === true) {
      this.fetchUsers();
      setUpdateBoolean(false);
    }
	}

	fetchMetaData() {
		fetch(URL + '/projects/' + this.props.match.params.project, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				name: responseJson.projectName,
				project: this.props.match.params.project
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	fetchUsers() {
		var url = URL;
		if (this.props.match.params.project !== '' && typeof this.props.match.params.project !== "undefined") {
			url += '/projects/' + this.props.match.params.project + '/users';
		} else {
			url += '/users';
		}
		return fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		if (this.props.match.params.project !== '' && typeof this.props.match.params.project !== "undefined") {
			return (
				//USERS OF SPECIFIC PROJECT
				<View>
					<View style={{flexDirection: 'row'}}>
						<View style={{flex:1}}>
							<Link to={"/projects/" + this.props.match.params.project} style={{textDecoration: 'none'}}>
							<Button
								onPress = { function doNothing() {} }
								title = {"Tickets of " + this.state.name}
								color = "#0e4a80"
							/>
							</Link>
						</View>
						<View style={{flex:1}}>
							<Button
								onPress = { function doNothing() {} }
								disabled = {true}
								title = {"Users of " + this.state.name}
							/>
						</View>
					</View>
					<ReactTable data={this.state.dataSource} noDataText="No Users found!" minRows={this.state.dataSource.length} showPagination={false} columns={ [
						{
							Header: 'Given Name',
							accessor: 'firstName'
						}, {
							Header: 'Surname',
							accessor: 'lastName'
						}, {
							Header: 'Login Name',
							accessor: 'loginName',
							show: false
						}, {
							Header: 'Password',
							accessor: 'password',
							show: false
						}, {
							Header: 'Phone Number',
							accessor: 'phone' // String-based value accessors!
						}, {
							Header: '',
							accessor: '',
							maxWidth: 35,
							Cell: props => <DeleteUserButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name} callToParent={this.fetchUsers.bind(this)}/>
						}
					] }/>
				</View>
			);
		}
		return(
			//GLOBAL USER MANAGEMENT
			<View>
				<View style={{flexDirection: 'row'}}>
					<View style={{flex:1}}>
						<Link to = "/" style={{textDecoration: 'none'}} >
						<Button
							title = "Projects"
							color = "#0e4a80"
						/>
						</Link>
					</View>
					<View style={{flex:1}}>
						<Button
							disabled = {true}
							title = {"Users"}
						/>
					</View>
				</View>
				<ReactTable data={this.state.dataSource} noDataText="No Users found!" minRows={this.state.dataSource.length} showPagination={false} columns={ [
					{
						Header: 'Given Name',
						accessor: 'firstName',
						Footer: props => <UserAdd project={this.state.project} name={this.state.name} callToParent={this.fetchUsers.bind(this)}/>
					}, {
						Header: 'Surname',
						accessor: 'lastName'
					}, {
						Header: 'Login Name',
						accessor: 'loginName',
						show: false
					}, {
						Header: 'Password',
						accessor: 'password',
						show: false
					}, {
						Header: 'Phone Number',
						accessor: 'phone' // String-based value accessors!
					}, {
						Header: '',
						accessor: '',
						maxWidth: 35,
						Cell: props => <UpdateUserButton proj={props} callToParent={this.fetchUsers.bind(this)}/>
					}, {
						Header: '',
						accessor: '',
						maxWidth: 35,
						Cell: props => <DeleteUserButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name} callToParent={this.fetchUsers.bind(this)}/>
					}
				] }/>
			</View>
		);
	}
}
