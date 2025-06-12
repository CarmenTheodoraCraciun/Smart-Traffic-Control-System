# **Smart Traffic Control System**
A **multi-agent system** that intelligently manages traffic flow at an road using autonomous agents developed in **JADE**.

## **📌 System Overview**
Each agent plays a crucial role in **optimizing road flow**:
- **Traffic Participant Agent (Vehicle/Pedestrian)** → Requests access to the intersection.
- **Emergency Agent** → Signals priority vehicles (e.g., ambulances, police cars).
- **Traffic Light Agent** → Controls the traffic light behavior.
- **Monitoring Agent** → Collects and analyzes traffic data.
- **Analysis & Reporting Agent** → Generates reports based on real-time data.

### **🚦 How It Works**
The system **simulates an urban environment** with vehicles, traffic sensors, and special scenarios.
- **Adaptive traffic lights:** Adjust signal durations based on traffic volume.
- **Emergency handling:** Ensures priority passage for emergency vehicles.
- **Real-time monitoring:** Tracks road congestion and optimizes waiting times.

## **🛠️ Installation & Setup**
### **1️⃣ Install JADE**
Ensure **JADE** is installed and configured. Verify the JADE path in:
- **File → Project Structure → Modules** (Check the JADE path, as shown in the image below).
- If missing, **click "Add" → Choose "JAR" → Locate the installed JADE folder** → Apply & close.

📷 **Example configuration:**  
![img_1.png](jade.png)  
![img.png](jade_module.png)

### **2️⃣ Configure Run Settings**
For the program to run correctly, verify the **Run → Edit Configuration** settings.  
📷 **Example:**  
![img.png](img.png)

### **3️⃣ Build & Run**
1. Open the project in **IntelliJ IDEA / Eclipse**.
2. Ensure all dependencies are installed.
3. **Run the `MainContainer`** to start the JADE agents.

## **🌍 Traffic Flow Interpretation**
### **Basic Pedestrian Crossing**
- **Linear traffic model**, no full intersection.
- Traffic lights control **vehicles moving in one direction only** (no turns).
- **Emergency vehicles always have priority**, regardless of light color.
