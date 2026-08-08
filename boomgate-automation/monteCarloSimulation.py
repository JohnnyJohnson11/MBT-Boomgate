import json
import random
import numpy as np
import matplotlib.pyplot as plt

# Your Graph JSON data
graph_json = {
    "models": [
        {
            "name": "LoginModel",
            "id": "m1",
            "startElementId": "v_dashboard",
            "vertices": [
                { "id": "v_adminDashboard", "name": "v_adminDashboard" }, 
                { "id": "v_satpamDashboard", "name": "v_satpamDashboard" }, 
                { "id": "v_wargaPageAdmin", "name": "v_wargaPageAdmin" },
                { "id": "v_wargaPageSatpam", "name": "v_wargaPageSatpam" },
                { "id": "v_createWargaDialog", "name": "v_createWargaDialog" },
                { "id": "v_editWargaDialog", "name": "v_editWargaDialog" }
            ],
            "edges": [
                { "id": "e1", "name": "e_openWargaPageAdmin", "sourceVertexId": "v_adminDashboard", "targetVertexId": "v_wargaPageAdmin" },
                { "id": "e2", "name": "e_openWargaPageSatpam", "sourceVertexId": "v_satpamDashboard", "targetVertexId": "v_wargaPageSatpam" },
                { "id": "e3", "name": "e_changeToAdmin", "sourceVertexId": "v_satpamDashboard", "targetVertexId": "v_adminDashboard" },
                { "id": "e4", "name": "e_changeToSatpam", "sourceVertexId": "v_adminDashboard", "targetVertexId": "v_satpamDashboard" },
                { "id": "e5", "name": "e_backToDashboardAdmin", "sourceVertexId": "v_wargaPageAdmin", "targetVertexId": "v_adminDashboard" },
                { "id": "e6", "name": "e_backToDashboardSatpam", "sourceVertexId": "v_wargaPageSatpam", "targetVertexId": "v_satpamDashboard" },
                { "id": "e7", "name": "e_openCreateDialog", "sourceVertexId": "v_wargaPageAdmin", "targetVertexId": "v_createWargaDialog" },
                { "id": "e8", "name": "e_createWarga", "sourceVertexId": "v_createWargaDialog", "targetVertexId": "v_wargaPageAdmin" },   
                { "id": "e11", "name": "e_deleteWarga", "sourceVertexId": "v_wargaPageAdmin", "targetVertexId": "v_wargaPageAdmin" }
            ]
        }
    ]
}

model = graph_json["models"][0]
vertices = model["vertices"]
edges = model["edges"]

# Step 1: Build the adjacency list for tracking outgoing edges
adjacency_list = {v["id"]: [] for v in vertices}
for edge in edges:
    adjacency_list[edge["sourceVertexId"]].append(edge)

all_edge_ids = set(e["id"] for e in edges)

# Step 2: Define the Monte Carlo simulation function
def run_monte_carlo(start_vertex, num_trials=10000):
    trial_steps = []
    
    for _ in range(num_trials):
        current_vertex = start_vertex
        visited_edges = set()
        steps = 0
        
        while len(visited_edges) < len(all_edge_ids):
            available_edges = adjacency_list[current_vertex]
            if not available_edges:
                # Fallback if a dead end is encountered
                steps = float('inf')
                break
            
            # Unbiased random choice among available outgoing paths
            chosen_edge = random.choice(available_edges)
            visited_edges.add(chosen_edge["id"])
            current_vertex = chosen_edge["targetVertexId"]
            steps += 1
            
        trial_steps.append(steps)
    return trial_steps

# Step 3: Run the simulation starting from v_adminDashboard
num_trials = 10000
results = run_monte_carlo("v_adminDashboard", num_trials=num_trials)

# Step 4: Calculate and output metrics
mean_steps = np.mean(results)
median_steps = np.median(results)
p90 = np.percentile(results, 90)
p95 = np.percentile(results, 95)

print(f"--- Simulation Results ({num_trials} trials) ---")
print(f"Minimum steps to cover all edges: {np.min(results)}")
print(f"Average (Mean) steps: {mean_steps:.2f}")
print(f"Median steps: {median_steps}")
print(f"90th Percentile: {p90}")
print(f"95th Percentile: {p95}")
print(f"Maximum steps observed: {np.max(results)}")

# Step 5: Plot distribution histogram
plt.hist(results, bins=range(9, int(p95) + 20), edgecolor='black', alpha=0.75, color='skyblue')
plt.title('Monte Carlo Simulation: Total Steps to Traverse All Edges')
plt.xlabel('Number of Steps')
plt.ylabel('Frequency')
plt.axvline(mean_steps, color='red', linestyle='dashed', linewidth=1.5, label=f'Mean: {mean_steps:.1f}')
plt.axvline(median_steps, color='green', linestyle='dashed', linewidth=1.5, label=f'Median: {median_steps:.1f}')
plt.legend()
plt.tight_layout()
plt.savefig('monte_carlo_distribution.png', dpi=300)