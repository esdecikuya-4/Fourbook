import subprocess
import math
import os

print("Generating 3D Modern Realistic Header for SDN 4 Putrajawa...")

W = 1600
H = 600

# 1. Base Sky Gradient
# Deep midnight sapphire at top -> royal blue -> warm dawn horizon glow
sky_cmd = [
    'convert', '-size', f'{W}x{H}',
    'gradient:#020617-#172554',
    '/tmp/layer_01_sky.png'
]
subprocess.run(sky_cmd, check=True)

# 2. Horizon Dawn Warm Glow (Radial & Linear blend)
horizon_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    '(', '-size', f'{W}x{int(H*0.55)}', 'gradient:rgba(217,119,6,0.45)-rgba(0,0,0,0)', ')',
    '-geometry', f'+0+{int(H*0.45)}', '-composite',
    '(', '-size', '1000x400', 'radial-gradient:rgba(251,191,36,0.50)-rgba(0,0,0,0)', ')',
    '-geometry', '+300+180', '-composite',
    '/tmp/layer_02_horizon.png'
]
subprocess.run(horizon_cmd, check=True)

# 3. Distant Mountains (Garut Volcanoes - Gunung Cikuray & Guntur silhouettes)
# Far Mountain Layer with atmospheric haze
far_mtn_points = "0,320 180,270 340,290 520,240 700,280 880,210 1050,260 1240,230 1420,275 1600,260 1600,420 0,420"
mid_mtn_points = "0,360 210,310 420,335 630,290 850,330 1080,280 1320,315 1490,295 1600,340 1600,430 0,430"

mtn_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Far mountain
    '-fill', '#0c2340', '-draw', f'polygon {far_mtn_points}',
    # Atmospheric mist over far mountain
    '(', '-size', f'{W}x120', 'gradient:rgba(186,230,253,0)-rgba(186,230,253,0.18)', ')',
    '-geometry', '+0+280', '-composite',
    # Mid mountain
    '-fill', '#071526', '-draw', f'polygon {mid_mtn_points}',
    '/tmp/layer_03_mountains.png'
]
subprocess.run(mtn_cmd, check=True)

# 4. Courtyard Plaza Floor (3D Paved Stone with Perspective Lines & Specular Glow)
ground_y = 370
floor_h = H - ground_y

# Create base paved ground
floor_cmd = [
    'convert', '-size', f'{W}x{floor_h}',
    'gradient:#0a1526-#040914',
    '/tmp/layer_04_ground_base.png'
]
subprocess.run(floor_cmd, check=True)

# Add perspective grid & wet specular reflection onto ground
vp_x = 800
vp_y = -30  # relative to floor top

grid_draws = []
# Perspective radial lines from vanishing point
for target_x in [-200, 0, 150, 300, 450, 600, 720, 800, 880, 1000, 1150, 1300, 1450, 1600, 1800]:
    grid_draws.extend(['-stroke', 'rgba(56,189,248,0.14)', '-strokewidth', '1.2',
                       '-draw', f'line {vp_x},{vp_y} {target_x},{floor_h}'])

# Horizontal distance lines with increasing density towards horizon
for d in [10, 25, 45, 75, 115, 165, 220]:
    if d < floor_h:
        opacity = 0.08 + (d / floor_h) * 0.16
        grid_draws.extend(['-stroke', f'rgba(56,189,248,{opacity:.2f})', '-strokewidth', '1.0',
                           '-draw', f'line 0,{d} {W},{d}'])

grid_cmd = ['convert', '/tmp/layer_04_ground_base.png'] + grid_draws + [
    # Wet specular reflection glow pools from entrance and windows
    '(', '-size', '700x120', 'radial-gradient:rgba(251,191,36,0.22)-rgba(0,0,0,0)', ')',
    '-geometry', '+450+10', '-composite',
    '(', '-size', '900x80', 'radial-gradient:rgba(56,189,248,0.18)-rgba(0,0,0,0)', ')',
    '-geometry', '+350+40', '-composite',
    '/tmp/layer_04_ground.png'
]
subprocess.run(grid_cmd, check=True)

# 5. Left Wing Building (Realistic 3D Perspective Receding Left)
# Coordinates
lw_wall = "120,225 460,205 460,378 120,378"
lw_roof = "80,215 475,195 475,215 80,235"
lw_eave = "70,215 480,195 480,203 70,223"

# Right Wing Building (Realistic 3D Perspective Receding Right)
rw_wall = "1140,205 1480,225 1480,378 1140,378"
rw_roof = "1125,195 1520,215 1520,235 1125,215"
rw_eave = "1120,195 1530,215 1530,223 1120,203"

wings_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Left wing wall (Modern Architectural Slate-Green Stucco)
    '-fill', '#12261b', '-draw', f'polygon {lw_wall}',
    # Left wing roof (Terracotta Clay Tile 3D bevel)
    '-fill', '#9a3412', '-draw', f'polygon {lw_roof}',
    '-fill', '#c2410c', '-draw', f'polygon {lw_eave}',
    # Right wing wall
    '-fill', '#12261b', '-draw', f'polygon {rw_wall}',
    # Right wing roof
    '-fill', '#9a3412', '-draw', f'polygon {rw_roof}',
    '-fill', '#c2410c', '-draw', f'polygon {rw_eave}',
    '/tmp/layer_05_wings.png'
]
subprocess.run(wings_cmd, check=True)

# 6. Wing Windows with Realistic Warm Classroom Interior Glow
windows_draws = []
# Left wing windows (6 modern glowing bays)
# Top floor
for (bx, by, bw, bh) in [(160, 235, 75, 48), (260, 230, 75, 48), (360, 225, 75, 48)]:
    windows_draws.extend([
        '-fill', '#0f172a', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#f59e0b', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        # Interior warm gradient light
        '-stroke', '#fbbf24', '-strokewidth', '1.5', '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-stroke', '#475569', '-strokewidth', '1', '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])
# Bottom floor
for (bx, by, bw, bh) in [(160, 300, 75, 52), (260, 298, 75, 52), (360, 295, 75, 52)]:
    windows_draws.extend([
        '-fill', '#0f172a', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#f59e0b', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        '-stroke', '#fbbf24', '-strokewidth', '1.5', '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-stroke', '#475569', '-strokewidth', '1', '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])

# Right wing windows
for (bx, by, bw, bh) in [(1165, 225, 75, 48), (1265, 230, 75, 48), (1365, 235, 75, 48)]:
    windows_draws.extend([
        '-fill', '#0f172a', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#f59e0b', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        '-stroke', '#fbbf24', '-strokewidth', '1.5', '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-stroke', '#475569', '-strokewidth', '1', '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])
for (bx, by, bw, bh) in [(1165, 295, 75, 52), (1265, 298, 75, 52), (1365, 300, 75, 52)]:
    windows_draws.extend([
        '-fill', '#0f172a', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#f59e0b', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        '-stroke', '#fbbf24', '-strokewidth', '1.5', '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-stroke', '#475569', '-strokewidth', '1', '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])

win_cmd = ['convert', '-size', f'{W}x{H}', 'xc:none'] + windows_draws + ['/tmp/layer_06_windows.png']
subprocess.run(win_cmd, check=True)

# 7. Main Central Smart Campus Building (Grand Double-Height Glass Facade & Architectural Elements)
# Center coordinates
c_left = 430
c_right = 1170
c_top = 160
c_bottom = 378

# Architectural Cantilevered Roof
c_roof_poly = f"540,165 670,100 930,100 1060,165"
c_roof_cap = f"660,96 940,96 940,102 660,102"
c_fascia_beam = f"400,162 1200,162 1200,178 400,178"

center_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Building Drop Shadow
    '-fill', 'rgba(0,0,0,0.45)', '-draw', f'roundrectangle {c_left-10},{c_top-5} {c_right+10},{c_bottom+12} 8,8',
    # Modern Clean Travertine White Concrete Wall
    '-fill', '#f1f5f9', '-draw', f'roundrectangle {c_left},{c_top} {c_right},{c_bottom} 6,6',
    # Top Pavilion Terracotta / Indigo Roof
    '-fill', '#c2410c', '-draw', f'polygon {c_roof_poly}',
    '-fill', '#ea580c', '-draw', f'polygon {c_roof_cap}',
    # Cantilevered Heavy Architectural Beam (Deep Charcoal)
    '-fill', '#0f172a', '-draw', f'roundrectangle 395,160 1205,178 4,4',
    # Modern Glowing Cyan LED Underside Edge Strip (Smart School Signature Accent)
    '-fill', '#38bdf8', '-draw', f'roundrectangle 400,178 1200,183 2,2',
    # Center Double-Height Glass Curtain Wall (Smart Tinted Solar Glass with Warm Glow)
    '-fill', '#0a2342', '-draw', 'roundrectangle 490,195 1110,368 5,5',
    # Warm interior classroom illumination behind glass
    '-fill', 'rgba(251,191,36,0.75)', '-draw', 'roundrectangle 500,205 1100,360 3,3',
    # Glass Diagonal Reflection Sheen
    '-fill', 'rgba(255,255,255,0.18)', '-draw', 'polygon 620,195 730,195 810,368 700,368',
    '-fill', 'rgba(56,189,248,0.22)', '-draw', 'polygon 820,195 910,195 980,368 890,368',
    # Architectural Aluminium Mullions / Structural Grid
    '-stroke', '#1e293b', '-strokewidth', '3.5',
    '-draw', 'line 590,195 590,368',
    '-draw', 'line 695,195 695,368',
    '-draw', 'line 800,195 800,368',
    '-draw', 'line 905,195 905,368',
    '-draw', 'line 1010,195 1010,368',
    '-draw', 'line 490,250 1110,250',
    '-draw', 'line 490,310 1110,310',
    '-stroke', 'none',
    '/tmp/layer_07_center.png'
]
subprocess.run(center_cmd, check=True)

# 8. Grand Entrance Canopy, Conical Volumetric Light Spill & Modern Glass Doors
entrance_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Conical light beam spilling onto plaza floor
    '-fill', 'rgba(251,191,36,0.30)', '-draw', 'polygon 720,368 880,368 1080,530 520,530',
    # Modern Glass Double Doors
    '-fill', '#0284c7', '-draw', 'roundrectangle 720,312 880,376 4,4',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 726,318 794,372 2,2',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 806,318 874,372 2,2',
    '-stroke', '#ffffff', '-strokewidth', '2.5', '-draw', 'line 800,312 800,376',
    # Entrance Canopy (Cantilevered Modern Royal Blue & Glass)
    '-stroke', 'none',
    '-fill', '#0369a1', '-draw', 'roundrectangle 695,304 905,314 3,3',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 700,314 900,317 1,1',
    '/tmp/layer_08_entrance.png'
]
subprocess.run(entrance_cmd, check=True)

# 9. 3D Architectural Signage: "SDN 4 PUTRAJAWA" & "FOURBOOK SMART CAMPUS"
# Beautiful 3D rendered billboard above main glass facade
sign_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Signboard backplate (Glossy Deep Blue with Gold Trim)
    '-fill', 'rgba(11,25,44,0.92)', '-draw', 'roundrectangle 540,128 1060,166 6,6',
    '-stroke', 'rgba(245,158,11,0.85)', '-strokewidth', '1.5',
    '-draw', 'roundrectangle 542,130 1058,164 5,5',
    '-stroke', 'none',
    # Glow behind text
    '(', '-size', '520x40', 'radial-gradient:rgba(56,189,248,0.45)-rgba(0,0,0,0)', ')',
    '-geometry', '+540+128', '-composite',
    # Text: SDN 4 PUTRAJAWA
    '-font', 'DejaVu-Sans-Bold',
    '-pointsize', '20',
    # Drop shadow
    '-fill', '#000000', '-annotate', '+632+154', 'SDN 4 PUTRAJAWA',
    # Chrome/Gold Face
    '-fill', '#f8fafc', '-annotate', '+630+152', 'SDN 4 PUTRAJAWA',
    # Golden Crest Stars
    '-fill', '#f59e0b', '-annotate', '+595+152', '★',
    '-fill', '#f59e0b', '-annotate', '+895+152', '★',
    '/tmp/layer_09_sign.png'
]
subprocess.run(sign_cmd, check=True)

# 10. Indonesian Flagpole & Realistically Waving Red & White Flag
# Placed proudly in the central-right plaza (x=980)
flag_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Flagpole Drop Shadow on ground
    '-fill', 'rgba(0,0,0,0.35)', '-draw', 'line 980,390 1040,430',
    # Tall Metallic Stainless Steel Pole (x=980, y=140 to 390)
    '-stroke', '#94a3b8', '-strokewidth', '4.0', '-draw', 'line 980,140 980,390',
    '-stroke', '#f8fafc', '-strokewidth', '1.5', '-draw', 'line 979,140 979,390',
    '-stroke', 'none',
    # Golden Finial Sphere on top
    '-fill', '#f59e0b', '-draw', 'circle 980,138 980,143',
    # Pole Pedestal / Stone Base
    '-fill', '#334155', '-draw', 'roundrectangle 972,385 988,393 2,2',
    '-fill', '#f8fafc', '-draw', 'roundrectangle 968,392 992,398 2,2',
    # Indonesian Flag (Waving gently to right)
    # Red Top Half with realistic cloth ripples
    '-fill', '#dc2626', '-draw', 'polygon 982,148 1070,142 1066,174 982,180',
    # Cloth shadow ripples (top red)
    '-fill', 'rgba(153,27,27,0.40)', '-draw', 'polygon 1010,146 1035,144 1032,176 1008,178',
    # White Bottom Half with ripples
    '-fill', '#f8fafc', '-draw', 'polygon 982,180 1066,174 1062,206 982,212',
    # Cloth shadow ripples (bottom white)
    '-fill', 'rgba(148,163,184,0.35)', '-draw', 'polygon 1010,178 1035,176 1032,208 1008,210',
    # Flag lanyard cord
    '-stroke', '#cbd5e1', '-strokewidth', '1.0', '-draw', 'line 982,148 982,212',
    '-stroke', 'none',
    '/tmp/layer_10_flag.png'
]
subprocess.run(flag_cmd, check=True)

# 11. Lush 3D Tropical Landscaping (Palm Trees, Layered Garden Shrubbery & Modern Bollard Lights)
plants_draws = []

# Left side tropical palm tree (Curved trunk + fronds)
# Trunk
plants_draws.extend([
    '-stroke', '#78350f', '-strokewidth', '6',
    '-draw', 'path "M 90,380 Q 75,300 110,220"',
    '-stroke', 'none'
])
# Palm Fronds
palm_center_x = 110
palm_center_y = 220
frond_targets = [
    (20, 210), (40, 180), (80, 160), (130, 155), (170, 170), (190, 205), (160, 235), (70, 240)
]
for (tx, ty) in frond_targets:
    plants_draws.extend([
        '-stroke', '#15803d', '-strokewidth', '3.5',
        '-draw', f'path "M {palm_center_x},{palm_center_y} Q {(palm_center_x+tx)//2},{(palm_center_y+ty)//2 - 15} {tx},{ty}"',
        '-stroke', '#22c55e', '-strokewidth', '1.5',
        '-draw', f'path "M {palm_center_x},{palm_center_y} Q {(palm_center_x+tx)//2},{(palm_center_y+ty)//2 - 15} {tx},{ty}"',
        '-stroke', 'none'
    ])

# Right side tropical palm tree
plants_draws.extend([
    '-stroke', '#78350f', '-strokewidth', '6',
    '-draw', 'path "M 1510,380 Q 1525,300 1490,220"',
    '-stroke', 'none'
])
palm2_x = 1490
palm2_y = 220
frond2_targets = [
    (1410, 205), (1430, 170), (1470, 155), (1520, 160), (1560, 180), (1580, 210), (1530, 240), (1450, 235)
]
for (tx, ty) in frond2_targets:
    plants_draws.extend([
        '-stroke', '#15803d', '-strokewidth', '3.5',
        '-draw', f'path "M {palm2_x},{palm2_y} Q {(palm2_x+tx)//2},{(palm2_y+ty)//2 - 15} {tx},{ty}"',
        '-stroke', '#22c55e', '-strokewidth', '1.5',
        '-draw', f'path "M {palm2_x},{palm2_y} Q {(palm2_x+tx)//2},{(palm2_y+ty)//2 - 15} {tx},{ty}"',
        '-stroke', 'none'
    ])

# Layered bushes / manicured hedges along building base
for (cx, cy, r, col) in [
    (60, 385, 45, '#14532d'), (110, 380, 38, '#166534'), (150, 388, 30, '#15803d'), (180, 392, 24, '#22c55e'),
    (400, 385, 32, '#14532d'), (430, 382, 28, '#166534'), (460, 388, 25, '#22c55e'),
    (1140, 388, 26, '#14532d'), (1170, 382, 30, '#166534'), (1200, 385, 32, '#22c55e'),
    (1420, 392, 24, '#15803d'), (1450, 388, 30, '#166534'), (1490, 380, 38, '#14532d'), (1540, 385, 45, '#14532d')
]:
    plants_draws.extend(['-fill', col, '-draw', f'circle {cx},{cy} {cx},{cy-r}'])

# Modern Glowing Pathway Light Bollards
for (bx, by) in [(280, 430), (480, 440), (1120, 440), (1320, 430)]:
    plants_draws.extend([
        # Bollard body
        '-stroke', '#334155', '-strokewidth', '4.0', '-draw', f'line {bx},{by} {bx},{by-22}',
        '-stroke', 'none',
        # Glowing LED head
        '-fill', '#38bdf8', '-draw', f'circle {bx},{by-23} {bx},{by-27}',
        # Ground pool of light
        '-fill', 'rgba(56,189,248,0.25)', '-draw', f'ellipse {bx},{by+2} 22,6 0,360'
    ])

plants_cmd = ['convert', '-size', f'{W}x{H}', 'xc:none'] + plants_draws + ['/tmp/layer_11_plants.png']
subprocess.run(plants_cmd, check=True)

# 12. Top atmospheric gradient vignette (ensuring crystal-clear readability for app buttons, search bar, and logos)
vignette_cmd = [
    'convert', '-size', f'{W}x{H}', 'xc:none',
    '(', '-size', f'{W}x220', 'gradient:rgba(3,7,18,0.72)-rgba(3,7,18,0)', ')',
    '-geometry', '+0+0', '-composite',
    # Left & Right subtle edge darkening
    '(', '-size', f'{W}x{H}', 'radial-gradient:rgba(0,0,0,0)-rgba(3,7,18,0.48)', ')',
    '-composite',
    '/tmp/layer_12_vignette.png'
]
subprocess.run(vignette_cmd, check=True)

# 13. MASTER COMPOSITE OF ALL 3D LAYERS
print("Compositing 3D Master Header Image...")
master_cmd = [
    'convert',
    '/tmp/layer_01_sky.png',
    '/tmp/layer_02_horizon.png', '-composite',
    '/tmp/layer_03_mountains.png', '-composite',
    '/tmp/layer_04_ground.png', '-geometry', f'+0+{ground_y}', '-composite',
    '/tmp/layer_05_wings.png', '-composite',
    '/tmp/layer_06_windows.png', '-composite',
    '/tmp/layer_07_center.png', '-composite',
    '/tmp/layer_08_entrance.png', '-composite',
    '/tmp/layer_09_sign.png', '-composite',
    '/tmp/layer_10_flag.png', '-composite',
    '/tmp/layer_11_plants.png', '-composite',
    '/tmp/layer_12_vignette.png', '-composite',
    '-quality', '96',
    '/tmp/sdn4_header_3d_realistic.png'
]
subprocess.run(master_cmd, check=True)

size_bytes = os.path.getsize('/tmp/sdn4_header_3d_realistic.png')
print(f"Master 3D Header Rendered Successfully: {size_bytes / 1024:.1f} KB")
