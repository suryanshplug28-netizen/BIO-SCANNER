Food Freshness & Hygiene Monitor (IoT & ML)
Overview
This project is an automated, hardware-software integrated system designed to detect food spoilage and verify utensil cleanliness in large-scale environments like college messes, hostels, and organizational cafeterias. By combining atmospheric gas detection, UV optical sensors, and machine learning, the system discriminates between healthy flora and harmful bacteria in real-time.

Project Roadmap & Architecture
Phase 1: Giving the Machine "Senses" (The Hardware)
To determine freshness and cleanliness without a full laboratory, the system relies on localized physical data collection.

The "Nose" (Gas Sensors): As bad bacteria multiply and food spoils, they release specific Volatile Organic Compounds (VOCs). Good bacteria (e.g., in yogurt) do not produce these rot-smelling compounds. We utilize an "Electronic Nose"—a cluster of affordable gas sensors (like the MQ series)—to detect these specific spoilage gases in the air right above the food.

The "Eyes" (Optical & UV Sensors): To verify washed plates, the system uses UV light sensors or ATP fluorescence. Bacteria, food residue, and organic waste glow under certain invisible light frequencies. The sensor reads this reflection: a completely clean plate reflects normally, while lingering bacteria trigger a distinct light pattern.

Phase 2: Building the "Brain" (Data & Machine Learning)
Sensors output raw voltage readings. A dedicated data pipeline and machine learning model translate these numbers into actionable insights.

Data Collection: Gas sensors are exposed to perfectly fresh, slightly aged, and completely spoiled food to record exact state readings. This process is repeated for clean and dirty plates using the UV sensors.

Training the Model: Historical data is fed into a machine learning algorithm that acts as a pattern-finder, learning the mathematical thresholds between safe and unsafe states.

The Output: Once trained, the model processes new, unseen sensor readings and instantly categorizes them as "Fresh," "Spoiled," "Clean," or "Contaminated."

Phase 3: Assembling the Physical Device
The physical prototype connects the sensory hardware to the ML processing unit.

The Microcontroller: A compact, affordable computer—such as a Raspberry Pi or an Arduino—serves as the core motherboard.

Integration & Wiring: The gas sensors and UV cameras are physically wired to the microcontroller for constant environmental reading.

Hosting the Brain (Edge ML): The trained machine learning model is loaded directly onto the microcontroller. This enables real-time, local processing of sensor data without requiring a constant internet connection.

Phase 4: Real-World Mess Hall Deployment
The final phase adapts the controlled prototype for a chaotic, humid kitchen environment.

Enclosure: The hardware is housed in a 3D-printed, waterproof, and heat-resistant case to survive kitchen hazards, splashes, and bumps.

Pilot Testing: The device is deployed in a live hostel cafeteria. Kitchen staff scan food batches and utensils, benchmarking the machine's predictions against standard visual inspections and professional lab swabs.

The Feedback Loop: Early misclassifications are recorded and fed back into the training dataset. The model is continuously retrained on this real-world data, ensuring high accuracy over time.

Author
Suryansh Rai

B.Sc. Data Science and Artificial Intelligence

Chandigarh University
