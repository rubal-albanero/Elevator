package com.elevator.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElevatorController {

	private int currentFloor = 0;
	private Direction direction = Direction.UP;
	private List<Integer> path = new ArrayList<>();

	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Set current floor and direction based on requested floor
	 * 
	 * @param currentFloor
	 * @throws InterruptedException 
	 */
	@PostMapping("/selectFloor/{floor}")
	public void selectFloor(@PathVariable int floor) throws InterruptedException {
		this.path.add(floor);
	}

	@GetMapping("/path")
	public List<Integer> getCurrentPath(){
		return this.path;
	}

	@PostMapping("/deSelectPath/{floor}")
	public void getDeSelectedPath(@PathVariable int floor){
		if(this.path.contains(floor)){
			this.path.remove(path.indexOf(floor));
		}
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@PostMapping("/callElevator")
	public @ResponseBody String callTheElevator(@RequestBody Action action) throws InterruptedException {
		//setCurrentFloor(action.floor);
		StringBuffer ssb = moveElevator(action);
		return "Current floor :: " + this.currentFloor +"\n"
		+ "Lift is going :: "+ action.direction +" to floor :: " + action.floor + "\n"+ ssb + "Lift reached :: "+ action.floor ;	
	}

	@PostMapping("/move")
	public StringBuffer moveElevator(@RequestBody(required = false) Action action) throws InterruptedException {
		StringBuffer sb = new StringBuffer();

		if(action == null) {
			List<Integer> list = new ArrayList<Integer>(path);
			//Collections.sort(list);
			// [1,3,4]

			if (this.currentFloor < list.get(0)) {
				setDirection(Direction.UP);
			} else {
				setDirection(Direction.DOWN);
			}
			// logic to halt at selected stops
			int counter = this.currentFloor;
			for(int stop: list) {
				counter++;
				if(counter == stop) {
					sb.append("Halting at floor :: " + counter + "\n");
					Thread.sleep(1000);
				}
				while(counter < stop)
				{
					sb.append("Lift Moving Up towards floor :: " + counter+ "\n");
					counter++;
					if(counter == stop) {
						sb.append("Halting at floor :: " + counter + "\n");
						Thread.sleep(1000);
					}
				}
				if(counter > stop)
				{
					counter=counter-2;
					while(counter > stop)
					{
						sb.append("Lift Moving Down towards floor :: " + counter+ "\n");
						counter--;
					}
					if(counter == stop) {
						sb.append("Halting at floor :: " + counter + "\n");
						Thread.sleep(1000);
					}
				}
			}          
			return sb;
		}

		int counter=this.currentFloor+1;

		while(counter < action.floor)
		{
			sb.append("Moving to floor" + counter + "\n");
			Thread.sleep(500);
			counter++;
		}
		return sb;
	}
}
