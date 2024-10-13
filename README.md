# sem3-pi-2024_25_G094

## Project Overview
This prodPlanSimulator.prodPlanSimulator.repository contains the code and prodPlanSimulator.resources for the Semester 3 Integrative Project, developed by Group G094. The project involves building a comprehensive solution for production management in industrial facilities, using various technologies such as PL/SQL, Java, C, and Assembly.

## Project Structure
The project is divided into several key modules, each focusing on different aspects of production management:

- **plantFloorManager**: Application to manage the plant floor, including workstations and operations. Implemented using PL/SQL scripts for Oracle.
- **machineSupervisor**: Supervisory system for monitoring and operating machines on the plant floor. Implemented using C and Assembly.
- **prodPlanSimulator**: Production Planner Simulator that simulates and optimizes production plans. Developed in Java.
- **projectManager**: Project management tool using PERT/CPM methodology for task and critical path management. Also developed in Java.

## Technologies Used
- **Languages**: Java, C, Assembly, PL/SQL
- **Database**: Oracle (PL/SQL)
- **Tools**: Visual Paradigm (data modeling), GitHub (version control)

## Setup Instructions
Follow these steps to set up and run each module of the project:

1. **Clone the prodPlanSimulator.prodPlanSimulator.repository**:
   ```bash
   git clone https://github.com/Departamento-de-Engenharia-Informatica/sem3-pi-2024-g094.git
   cd integrative-project
   ```

2. **Database Setup (plantFloorManager)**:
    - Ensure access to Oracle.
    - Navigate to the `plantFloorManager` directory and run the PL/SQL scripts to create and populate the necessary tables.

3. **Running the Machine Supervisor (machineSupervisor)**:
    - Navigate to the `machineSupervisor` folder.
    - Compile the C and Assembly files, then run the application to supervise machine operations.

4. **Running the Production Planner Simulator (prodPlanSimulator)**:
    - Navigate to the `prodPlanSimulator` folder.
    - Compile and run the Java application to simulate and plan production processes.

5. **Running the Project Manager (projectManager)**:
    - Navigate to the `projectManager` folder.
    - Compile and run the Java application to manage tasks using PERT/CPM.

## Sprint Planning
The project is divided into 3 sprints:

- **Sprint 1**: Weeks 3 to 6 – from 1/October to 27/October
- **Sprint 2**: Weeks 7 to 10 – from 28/October to 24/November
- **Sprint 3**: Weeks 11 to 14 – from 25/November to 11/January

## Contributing
1. Fork the prodPlanSimulator.prodPlanSimulator.repository.
2. Create your feature branch:
   ```bash
   git checkout -b feature/YourFeature
   ```
3. Commit your changes:
   ```bash
   git commit -m 'Add some feature'
   ```
4. Push to the branch:
   ```bash
   git push origin feature/YourFeature
   ```
5. Create a pull request.
