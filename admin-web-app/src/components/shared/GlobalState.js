var reg = [];
var state = {};

export function registerFunc(func){
	if (!(typeof(func)==="function"))
		alert ("error");
	reg.push(func);
}

function runFunc () {
	reg.forEach(func => {func()});
}

export function setState (newState){
	//set state here
	state = newState;
	runFunc();
}

export function getState(){return state;}
