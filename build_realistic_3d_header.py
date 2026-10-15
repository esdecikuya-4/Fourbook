import subprocess
import math
import os

print("Generating Ultra-Realistic 3D Modern Architectural Header...")

W = 1600
H = 600
ground_y = 355
floor_h = H - ground_y

os.makedirs("tmp_build", exist_ok=True)

# 1. Sky: Cinematic Morning Dawn / Twilight Sky
# Deep sapphire space at top -> royal blue -> glowing cyan -> brilliant golden sunrise horizon
subprocess.run([
    'convert', '-size', f'{W}x{H}',
    'gradient:#030712-#0a2558',
    'tmp_build/01_sky_base.png'
], check=True)

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Golden dawn horizon glow
    '(', '-size', f'{W}x280', 'gradient:rgba(245,158,11,0.55)-rgba(2,132,199,0)', ')',
    '-geometry', f'+0+{ground_y-180}', '-composite',
    # Sun core glow behind mountains
    '(', '-size', '1100x420', 'radial-gradient:rgba(254,240,138,0.70)-rgba(245,158,11,0)', ')',
    '-geometry', f'+250+{ground_y-260}', '-composite',
    # Volumetric cyan atmospheric aura
    '(', '-size', '1400x300', 'radial-gradient:rgba(56,189,248,0.28)-rgba(0,0,0,0)', ')',
    '-geometry', f'+100+{ground_y-200}', '-composite',
    'tmp_build/02_sky_glow.png'
], check=True)

# 2. Majestic Mountain Ridges (Garut Volcanic Peaks - Gunung Cikuray)
subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Distant peak with atmospheric haze
    '-fill', '#0e2444',
    '-draw', 'polygon 0,280 160,230 320,250 510,185 680,245 840,155 1020,225 1200,175 1410,240 1600,210 1600,380 0,380',
    # Mist over distant peaks
    '(', '-size', f'{W}x100', 'gradient:rgba(186,230,253,0)-rgba(186,230,253,0.22)', ')',
    '-geometry', f'+0+{ground_y-130}', '-composite',
    # Mid-ground mountain ridge (rich dark indigo)
    '-fill', '#07152b',
    '-draw', 'polygon 0,320 220,270 410,300 620,240 820,290 1060,230 1280,285 1470,255 1600,310 1600,390 0,390',
    # Mountain ridge rim highlight (sunlight edge)
    '-stroke', 'rgba(251,191,36,0.35)', '-strokewidth', '1.5',
    '-draw', 'path "M 510,185 L 680,245 L 840,155 L 1020,225 L 1200,175"',
    '-stroke', 'none',
    'tmp_build/03_mountains.png'
], check=True)

# 3. 3D Courtyard Plaza Floor (Granite Flagstones with 3D Wet Specular Reflections)
grid_draws = []
vp_x = 800
vp_y = ground_y - 40

# Radial perspective flagstone joint lines
for tx in range(-300, W + 350, 100):
    grid_draws.extend(['-stroke', 'rgba(56,189,248,0.18)', '-strokewidth', '1.2',
                       '-draw', f'line {vp_x},{vp_y} {tx},{H}'])

# Horizontal depth lines
for d in [8, 20, 36, 58, 86, 122, 168, 224]:
    if ground_y + d < H:
        y_pos = ground_y + d
        op = 0.12 + (d / floor_h) * 0.22
        grid_draws.extend(['-stroke', f'rgba(56,189,248,{op:.2f})', '-strokewidth', '1.2',
                           '-draw', f'line 0,{y_pos} {W},{y_pos}'])

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Base ground gradient
    '(', '-size', f'{W}x{floor_h}', 'gradient:#081324-#020610', ')',
    '-geometry', f'+0+{ground_y}', '-composite',
    # Grid lines
] + grid_draws + [
    # Wet specular golden reflection pool under entrance & windows
    '(', '-size', '800x160', 'radial-gradient:rgba(245,158,11,0.28)-rgba(0,0,0,0)', ')',
    '-geometry', f'+400+{ground_y}', '-composite',
    # Cyan sky ambient reflection pool
    '(', '-size', '1000x120', 'radial-gradient:rgba(56,189,248,0.20)-rgba(0,0,0,0)', ')',
    '-geometry', f'+300+{ground_y+30}', '-composite',
    'tmp_build/04_ground.png'
], check=True)

# 4. Realistic 3D Campus Wings (Left & Right - 2-Story Modern Architectural Wings)
# Left wing perspective
lw_wall = "100,210 450,190 450,365 100,365"
lw_roof_top = "60,195 465,175 465,195 60,215"
lw_roof_shd = "60,215 465,195 465,203 60,223"

# Right wing perspective
rw_wall = "1150,190 1500,210 1500,365 1150,365"
rw_roof_top = "1135,175 1540,195 1540,215 1135,195"
rw_roof_shd = "1135,195 1540,215 1540,223 1135,203"

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Ambient Occlusion Drop Shadow behind wings
    '-fill', 'rgba(0,0,0,0.55)',
    '-draw', 'polygon 90,205 460,185 460,372 90,372',
    '-draw', 'polygon 1140,185 1510,205 1510,372 1140,372',
    # Left wing wall (Modern Architectural Sage-Slate Concrete Panel)
    '-fill', '#102419', '-draw', f'polygon {lw_wall}',
    # Left wing roof (Terracotta Clay Roof with warm sun highlight)
    '-fill', '#c2410c', '-draw', f'polygon {lw_roof_top}',
    '-fill', '#7c2d12', '-draw', f'polygon {lw_roof_shd}',
    # Left wing architectural cyan trim
    '-stroke', 'rgba(56,189,248,0.7)', '-strokewidth', '2',
    '-draw', 'line 60,223 465,203',
    '-stroke', 'none',

    # Right wing wall
    '-fill', '#102419', '-draw', f'polygon {rw_wall}',
    # Right wing roof
    '-fill', '#c2410c', '-draw', f'polygon {rw_roof_top}',
    '-fill', '#7c2d12', '-draw', f'polygon {rw_roof_shd}',
    # Right wing architectural cyan trim
    '-stroke', 'rgba(56,189,248,0.7)', '-strokewidth', '2',
    '-draw', 'line 1135,203 1540,223',
    '-stroke', 'none',
    'tmp_build/05_wings_base.png'
], check=True)

# 5. Wing Windows with Real Glass Depth & Interior Warm Classrooms
wing_win_draws = []
# Left wing windows (6 large modern bays)
# Upper floor
for bx, by, bw, bh in [(140, 225, 80, 48), (245, 220, 80, 48), (350, 215, 80, 48)]:
    wing_win_draws.extend([
        # Window frame shadow
        '-fill', '#0a0f1d', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        # Glass backing (deep solar glass)
        '-fill', '#0c1e38', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        # Warm classroom interior lamp glow
        '-fill', 'rgba(251,191,36,0.85)', '-draw', f'roundrectangle {bx+4},{by+4} {bx+bw-4},{by+bh-4} 2,2',
        # Glass diagonal specular reflection sheen
        '-fill', 'rgba(255,255,255,0.35)', '-draw', f'polygon {bx+10},{by} {bx+32},{by} {bx+22},{by+bh} {bx},{by+bh}',
        # Modern black aluminium mullions
        '-stroke', '#1e293b', '-strokewidth', '2.0',
        '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])
# Lower floor
for bx, by, bw, bh in [(140, 290, 80, 55), (245, 287, 80, 55), (350, 284, 80, 55)]:
    wing_win_draws.extend([
        '-fill', '#0a0f1d', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#0c1e38', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        '-fill', 'rgba(251,191,36,0.85)', '-draw', f'roundrectangle {bx+4},{by+4} {bx+bw-4},{by+bh-4} 2,2',
        '-fill', 'rgba(255,255,255,0.35)', '-draw', f'polygon {bx+10},{by} {bx+32},{by} {bx+22},{by+bh} {bx},{by+bh}',
        '-stroke', '#1e293b', '-strokewidth', '2.0',
        '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])

# Right wing windows
for bx, by, bw, bh in [(1170, 215, 80, 48), (1275, 220, 80, 48), (1380, 225, 80, 48),
                       (1170, 284, 80, 55), (1275, 287, 80, 55), (1380, 290, 80, 55)]:
    wing_win_draws.extend([
        '-fill', '#0a0f1d', '-draw', f'roundrectangle {bx-2},{by-2} {bx+bw+2},{by+bh+2} 3,3',
        '-fill', '#0c1e38', '-draw', f'roundrectangle {bx},{by} {bx+bw},{by+bh} 2,2',
        '-fill', 'rgba(251,191,36,0.85)', '-draw', f'roundrectangle {bx+4},{by+4} {bx+bw-4},{by+bh-4} 2,2',
        '-fill', 'rgba(255,255,255,0.35)', '-draw', f'polygon {bx+10},{by} {bx+32},{by} {bx+22},{by+bh} {bx},{by+bh}',
        '-stroke', '#1e293b', '-strokewidth', '2.0',
        '-draw', f'line {bx+bw//2},{by} {bx+bw//2},{by+bh}',
        '-draw', f'line {bx},{by+bh//2} {bx+bw},{by+bh//2}',
        '-stroke', 'none'
    ])

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none'
] + wing_win_draws + ['tmp_build/06_wing_windows.png'], check=True)

# 6. Central Main Campus Building (Modern Architectural Travertine Facade & Glass Curtain Wall)
c_left, c_right = 420, 1180
c_top, c_bottom = 145, 365

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Deep ambient occlusion drop shadow onto ground & background
    '-fill', 'rgba(0,0,0,0.60)',
    '-draw', f'roundrectangle {c_left-15},{c_top-10} {c_right+15},{c_bottom+16} 10,10',
    # Modern Clean Travertine White Concrete Wall
    '-fill', '#f8fafc',
    '-draw', f'roundrectangle {c_left},{c_top} {c_right},{c_bottom} 6,6',
    # Architectural Concrete Panel Seam Lines (Modern modular joints)
    '-stroke', 'rgba(203,213,225,0.65)', '-strokewidth', '1.0',
    '-draw', f'line {c_left},{c_top+80} {c_right},{c_top+80}',
    '-draw', f'line {c_left},{c_top+140} {c_right},{c_top+140}',
    '-draw', f'line {c_left+160},{c_top} {c_left+160},{c_bottom}',
    '-draw', f'line {c_right-160},{c_top} {c_right-160},{c_bottom}',
    '-stroke', 'none',

    # Center Upper Roof (Modern High Pavilion Pitch with Clay Tile Shading)
    '-fill', '#9a3412', '-draw', 'polygon 520,145 660,82 940,82 1080,145',
    '-fill', '#c2410c', '-draw', 'polygon 535,145 668,88 932,88 1065,145',
    # Golden Architectural Ridge Beam on roof apex
    '-fill', '#f59e0b', '-draw', 'roundrectangle 650,78 950,86 4,4',
    '-fill', '#fef08a', '-draw', 'roundrectangle 660,80 940,83 2,2',

    # Cantilevered Architectural Overhang Fascia Beam (Sleek Deep Navy)
    '-fill', '#0f172a', '-draw', 'roundrectangle 380,145 1220,165 4,4',
    # Glowing Cyan LED Underside Trim (Modern Smart School Aesthetic)
    '-fill', '#38bdf8', '-draw', 'roundrectangle 385,165 1215,170 2,2',
    # Soft blue glow blur beneath LED strip
    '-stroke', 'rgba(56,189,248,0.45)', '-strokewidth', '4.0',
    '-draw', 'line 390,172 1210,172',
    '-stroke', 'none',
    'tmp_build/07_center_structure.png'
], check=True)

# 7. Grand Double-Height Glass Curtain Wall (Smart Tinted Solar Glass with Warm Interior Lighting)
gw_left, gw_right = 480, 1120
gw_top, gw_bottom = 180, 355

curtain_draws = [
    # Frame recess shadow
    '-fill', '#090d16', '-draw', f'roundrectangle {gw_left-4},{gw_top-4} {gw_right+4},{gw_bottom+4} 6,6',
    # Base Glass Curtain (Deep Royal Solar Tint)
    '-fill', '#0a2540', '-draw', f'roundrectangle {gw_left},{gw_top} {gw_right},{gw_bottom} 4,4',
    # Warm glowing interior classrooms behind the glass
    '-fill', 'rgba(251,191,36,0.82)', '-draw', f'roundrectangle {gw_left+6},{gw_top+6} {gw_right-6},{gw_bottom-6} 3,3',
    # Volumetric Glass Specular Shimmer Diagonal Strips
    '-fill', 'rgba(255,255,255,0.25)', '-draw', f'polygon 620,{gw_top} 740,{gw_top} 820,{gw_bottom} 700,{gw_bottom}',
    '-fill', 'rgba(56,189,248,0.30)', '-draw', f'polygon 850,{gw_top} 950,{gw_top} 1010,{gw_bottom} 910,{gw_bottom}',
    '-fill', 'rgba(255,255,255,0.18)', '-draw', f'polygon 510,{gw_top} 560,{gw_top} 600,{gw_bottom} 550,{gw_bottom}',
]

# Aluminium Structural Window Grid / Mullions (Floor-to-ceiling modern grid)
# Vertical Mullions
for mx in [560, 640, 720, 800, 880, 960, 1040]:
    curtain_draws.extend([
        '-stroke', '#0f172a', '-strokewidth', '3.5', '-draw', f'line {mx},{gw_top} {mx},{gw_bottom}',
        '-stroke', 'rgba(255,255,255,0.2)', '-strokewidth', '1.0', '-draw', f'line {mx-1},{gw_top} {mx-1},{gw_bottom}'
    ])
# Horizontal Floor Transoms
for my in [238, 296]:
    curtain_draws.extend([
        '-stroke', '#0f172a', '-strokewidth', '4.0', '-draw', f'line {gw_left},{my} {gw_right},{my}',
        '-stroke', 'rgba(255,255,255,0.2)', '-strokewidth', '1.0', '-draw', f'line {gw_left},{my-1} {gw_right},{my-1}'
    ])
curtain_draws.extend(['-stroke', 'none'])

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none'
] + curtain_draws + ['tmp_build/08_curtain_wall.png'], check=True)

# 8. Grand Entrance Portico, Glass Double Doors & Volumetric Light Beam
subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Warm golden conical light beam spilling onto plaza floor
    '-fill', 'rgba(251,191,36,0.35)', '-draw', f'polygon 730,{gw_bottom} 870,{gw_bottom} 1080,{H-40} 520,{H-40}',
    # Glass automatic sliding double doors
    '-fill', '#0284c7', '-draw', 'roundrectangle 720,296 880,358 4,4',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 726,302 794,354 2,2',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 806,302 874,354 2,2',
    '-stroke', '#ffffff', '-strokewidth', '2.5', '-draw', 'line 800,296 800,358',
    # Entrance Canopy (Modern Cantilevered Blue & Chrome)
    '-stroke', 'none',
    '-fill', '#0369a1', '-draw', 'roundrectangle 695,290 905,299 3,3',
    '-fill', '#38bdf8', '-draw', 'roundrectangle 700,299 900,302 1,1',
    # Recessed spotlight downlights under canopy
    '-fill', '#fef08a', '-draw', 'circle 750,299 750,301',
    '-fill', '#fef08a', '-draw', 'circle 850,299 850,301',
    'tmp_build/09_entrance.png'
], check=True)

# 9. Modern 3D Architectural Signboard: "SDN 4 PUTRAJAWA"
subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Signboard backplate (Glossy Deep Blue with Gold Trim)
    '-fill', 'rgba(11,25,44,0.94)', '-draw', 'roundrectangle 530,108 1070,146 6,6',
    '-stroke', 'rgba(245,158,11,0.90)', '-strokewidth', '1.5',
    '-draw', 'roundrectangle 532,110 1068,144 5,5',
    '-stroke', 'none',
    # Ambient cyan glow behind sign
    '(', '-size', '540x40', 'radial-gradient:rgba(56,189,248,0.50)-rgba(0,0,0,0)', ')',
    '-geometry', '+530+108', '-composite',
    # Text: SDN 4 PUTRAJAWA with 3D Bevel & Drop Shadow
    '-font', '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf',
    '-pointsize', '20',
    # Drop shadow
    '-fill', '#000000', '-draw', 'text 642,134 "SDN 4 PUTRAJAWA"',
    # Metallic gold / white face
    '-fill', '#f8fafc', '-draw', 'text 640,132 "SDN 4 PUTRAJAWA"',
    # Golden stars
    '-fill', '#f59e0b', '-draw', 'text 595,132 "★"',
    '-fill', '#f59e0b', '-draw', 'text 985,132 "★"',
    'tmp_build/10_sign.png'
], check=True)

# 10. Indonesian National Flagpole & Realistic Waving Merah Putih Flag
subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    # Flagpole shadow on ground
    '-stroke', 'rgba(0,0,0,0.40)', '-strokewidth', '3', '-draw', 'line 980,370 1040,410',
    '-stroke', 'none',
    # Tall stainless steel metallic pole (shiny specular highlight)
    '-stroke', '#94a3b8', '-strokewidth', '4.0', '-draw', 'line 980,120 980,370',
    '-stroke', '#f8fafc', '-strokewidth', '1.5', '-draw', 'line 979,120 979,370',
    '-stroke', 'none',
    # Golden finial sphere at apex
    '-fill', '#f59e0b', '-draw', 'circle 980,118 980,123',
    # Marble base pedestal
    '-fill', '#334155', '-draw', 'roundrectangle 972,365 988,373 2,2',
    '-fill', '#f8fafc', '-draw', 'roundrectangle 968,372 992,378 2,2',

    # Indonesian Flag (Waving dynamically to the right)
    # Red top half with 3D ripple curves
    '-fill', '#dc2626', '-draw', 'polygon 982,128 1075,122 1070,154 982,160',
    # Red wave shadows
    '-fill', 'rgba(153,27,27,0.45)', '-draw', 'polygon 1010,126 1038,124 1035,156 1007,158',
    # White bottom half with 3D ripple curves
    '-fill', '#f8fafc', '-draw', 'polygon 982,160 1070,154 1065,186 982,192',
    # White wave shadows
    '-fill', 'rgba(148,163,184,0.38)', '-draw', 'polygon 1010,158 1038,156 1035,188 1007,190',
    # Lanyard rope
    '-stroke', '#cbd5e1', '-strokewidth', '1.0', '-draw', 'line 982,128 982,192',
    '-stroke', 'none',
    'tmp_build/11_flag.png'
], check=True)

# 11. 3D Tropical Landscaping (Curved Palm Trees with layered fronds, manicured bushes, glowing bollards)
plants_draws = [
    # Left Palm Trunk (Rich textured curved palm wood)
    '-stroke', '#78350f', '-strokewidth', '7.0', '-draw', 'path "M 80,375 Q 65,290 105,200"',
    '-stroke', '#b45309', '-strokewidth', '2.5', '-draw', 'path "M 82,375 Q 67,290 107,200"',
    '-stroke', 'none'
]

# Left Palm Fronds with layered sunlit green
p1x, p1y = 105, 200
for tx, ty in [(15, 190), (35, 160), (75, 140), (125, 135), (165, 150), (185, 185), (155, 215), (65, 220)]:
    mx, my = (p1x + tx) // 2, (p1y + ty) // 2 - 18
    plants_draws.extend([
        '-stroke', '#14532d', '-strokewidth', '4.5', '-draw', f'path "M {p1x},{p1y} Q {mx},{my} {tx},{ty}"',
        '-stroke', '#22c55e', '-strokewidth', '2.0', '-draw', f'path "M {p1x},{p1y} Q {mx},{my} {tx},{ty}"',
        '-stroke', 'none'
    ])

# Right Palm Trunk & Fronds
plants_draws.extend([
    '-stroke', '#78350f', '-strokewidth', '7.0', '-draw', 'path "M 1520,375 Q 1535,290 1495,200"',
    '-stroke', '#b45309', '-strokewidth', '2.5', '-draw', 'path "M 1518,375 Q 1533,290 1493,200"',
    '-stroke', 'none'
])
p2x, p2y = 1495, 200
for tx, ty in [(1415, 185), (1435, 150), (1475, 135), (1525, 140), (1565, 160), (1585, 190), (1535, 220), (1445, 215)]:
    mx, my = (p2x + tx) // 2, (p2y + ty) // 2 - 18
    plants_draws.extend([
        '-stroke', '#14532d', '-strokewidth', '4.5', '-draw', f'path "M {p2x},{p2y} Q {mx},{my} {tx},{ty}"',
        '-stroke', '#22c55e', '-strokewidth', '2.0', '-draw', f'path "M {p2x},{p2y} Q {mx},{my} {tx},{ty}"',
        '-stroke', 'none'
    ])

# Lush layered bushes with 3D shading
for cx, cy, r, base_col, high_col in [
    (50, 375, 45, '#14532d', '#16a34a'), (100, 370, 38, '#166534', '#22c55e'), (140, 378, 30, '#15803d', '#4ade80'),
    (390, 375, 32, '#14532d', '#16a34a'), (420, 372, 28, '#166534', '#22c55e'), (450, 378, 25, '#15803d', '#4ade80'),
    (1150, 378, 25, '#15803d', '#4ade80'), (1180, 372, 28, '#166534', '#22c55e'), (1210, 375, 32, '#14532d', '#16a34a'),
    (1460, 378, 30, '#15803d', '#4ade80'), (1500, 370, 38, '#166534', '#22c55e'), (1550, 375, 45, '#14532d', '#16a34a')
]:
    plants_draws.extend([
        '-fill', base_col, '-draw', f'circle {cx},{cy} {cx},{cy-r}',
        '-fill', high_col, '-draw', f'circle {cx-4},{cy-8} {cx-4},{cy-r+10}'
    ])

# Glowing pathway bollards
for bx, by in [(260, 420), (460, 430), (1140, 430), (1340, 420)]:
    plants_draws.extend([
        '-stroke', '#334155', '-strokewidth', '4.0', '-draw', f'line {bx},{by} {bx},{by-22}',
        '-stroke', 'none',
        '-fill', '#38bdf8', '-draw', f'circle {bx},{by-23} {bx},{by-27}',
        '-fill', 'rgba(56,189,248,0.30)', '-draw', f'ellipse {bx},{by+2} 24,7 0,360'
    ])

subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none'
] + plants_draws + ['tmp_build/12_plants.png'], check=True)

# 12. Top & Corner Vignette (Ensures header buttons, tabs, avatar, and search remain 100% legible)
subprocess.run([
    'convert', '-size', f'{W}x{H}', 'xc:none',
    '(', '-size', f'{W}x220', 'gradient:rgba(3,7,18,0.70)-rgba(3,7,18,0)', ')',
    '-geometry', '+0+0', '-composite',
    '(', '-size', f'{W}x{H}', 'radial-gradient:rgba(0,0,0,0)-rgba(3,7,18,0.45)', ')',
    '-composite',
    'tmp_build/13_vignette.png'
], check=True)

# 13. MASTER COMPOSITE (Combines all layers into the pristine 3D master image)
print("Compositing all 3D layers...")
subprocess.run([
    'convert',
    'tmp_build/01_sky_base.png',
    'tmp_build/02_sky_glow.png', '-composite',
    'tmp_build/03_mountains.png', '-composite',
    'tmp_build/04_ground.png', '-composite',
    'tmp_build/05_wings_base.png', '-composite',
    'tmp_build/06_wing_windows.png', '-composite',
    'tmp_build/07_center_structure.png', '-composite',
    'tmp_build/08_curtain_wall.png', '-composite',
    'tmp_build/09_entrance.png', '-composite',
    'tmp_build/10_sign.png', '-composite',
    'tmp_build/11_flag.png', '-composite',
    'tmp_build/12_plants.png', '-composite',
    'tmp_build/13_vignette.png', '-composite',
    '-quality', '96',
    'sdn4_header_3d_realistic.png'
], check=True)

size_kb = os.path.getsize('sdn4_header_3d_realistic.png') / 1024
print(f"Master 3D Header Rendered Successfully: {size_kb:.1f} KB")
