# Smart-Traffic-Control-System

Smart Traffic Control System – An intelligent traffic management system in an intersection, using autonomous agents developed in JADE:
   - Vehicle Agent         – ​​requests access to the intersection.
   - Traffic Light Agent – ​​controls the traffic light status.
   - Monitoring Agent   – ​​collects traffic data.
   - Planning Agent      – ​​decides the duration of traffic lights.
   - Emergency Agent  – ​​signals priority vehicles.

Each agent has a specific role in optimizing road flow. Agents communicate with each other to adapt the duration of traffic lights in real time according to the traffic volume and to give priority to emergency vehicles (e.g. ambulances). The system simulates an urban environment with vehicles, traffic sensors, traffic lights and special situations with the aim of reducing waiting time and congestion at the intersection.

Each submission should contain a readme file which contains instructions on how you can install and run your application as well as a short description of it.

Install

Need to verify in File -> Project Structure -> Modules if you have the pat to Jade, like in the image. If not, click on add, choose JAR and go to the path where Jade is insall. Apply and close.
![img_1.png](jade.png)
![img.png](jade_module.png)

Also, for run to work you need to verify in Run -> Edit Configuration to look like in the picture.

![img.png](img.png)