package com.example.data.repository

import com.example.R
import com.example.data.model.DrawingStep
import com.example.data.model.DrawingSubject
import com.example.data.model.StepPhase

object DrawingLibraryRepository {

    val curatedSubjects: List<DrawingSubject> = listOf(
        DrawingSubject(
            id = "fox_beginner",
            title = "Cute Baby Fox",
            category = "Animals",
            difficulty = "Beginner",
            estimatedMinutes = 15,
            drawableResId = R.drawable.ref_fox,
            description = "Learn classical animal proportion marks starting from simple head circles and body ovals.",
            steps = listOf(
                DrawingStep(
                    stepIndex = 1,
                    title = "Head Circle & Body Oval",
                    phase = StepPhase.GEOMETRIC_BASES,
                    instruction = "Draw a gentle, light circle for the head in the upper center of your page. Just below it, draw a slightly larger tilted oval for the sitting body.",
                    spokenCoachingEn = "Let's begin with our base shapes! Lightly sketch a circle near the top center for the head, then an oval below it for the body. Keep your pencil pressure very light!",
                    spokenCoachingHinglish = "Chaliye shuru karte hain! Paper ke upper-center me halka sa ek circle banayein head ke liye, aur theek neeche ek oval body ke liye. Pencil ko halka rakhein!",
                    tips = listOf("Hold your pencil loosely near the middle", "Do not press hard; guide lines will be erased or blended later", "Head to body proportion is about 1:1.5"),
                    strokeDiagramDesc = "Circle on top (diameter ~5cm), tilted egg-oval below (height ~7cm)",
                    targetGeometricShapes = listOf("Circle", "Oval", "Center vertical axis")
                ),
                DrawingStep(
                    stepIndex = 2,
                    title = "Ear Triangles & Spine Line",
                    phase = StepPhase.SECONDARY_GUIDES,
                    instruction = "Add two soft triangle guides on top of the head circle for the ears. Connect the head and body with a gentle S-curve representing the fox's spine.",
                    spokenCoachingEn = "Awesome start! Now place two rounded triangles on top of the circle for the ears, angled slightly outward. Then sketch a smooth curve connecting head to lower back.",
                    spokenCoachingHinglish = "Boht badiya! Ab head circle ke upar do soft triangles banayein kaano ke liye. Fir gardan se peeth tak ek smooth curved line draw karein.",
                    tips = listOf("Ears should point outward like 10 o'clock and 2 o'clock", "Keep the spine curve soft and natural"),
                    strokeDiagramDesc = "Two angled triangles at 45 degrees on top of head circle",
                    targetGeometricShapes = listOf("Two triangles", "Connecting spine curve")
                ),
                DrawingStep(
                    stepIndex = 3,
                    title = "Muzzle V-Guide & Front Paws",
                    phase = StepPhase.PRIMARY_OUTLINES,
                    instruction = "Draw a soft inverted triangle/wedge in the lower third of the head circle for the snout. Drop two parallel vertical lines down for the front legs.",
                    spokenCoachingEn = "Looking great! Let's shape the face and stance. Draw a small downward wedge for the snout, and sketch two straight vertical guides for the front paws.",
                    spokenCoachingHinglish = "Shabash! Ab face ka shape banate hain. Head circle ke neeche ek chhota sa wedge snout ke liye banayein, aur aage ke pairo ke liye do lines neeche draw karein.",
                    tips = listOf("The snout wedge aligns with the center facial axis", "Front paws should rest on a flat imaginary ground line"),
                    strokeDiagramDesc = "Center snout triangle and two parallel leg cylinders",
                    targetGeometricShapes = listOf("Wedge/Triangle snout", "Parallel vertical leg lines")
                ),
                DrawingStep(
                    stepIndex = 4,
                    title = "Bushy Tail Arc & Eyes",
                    phase = StepPhase.DEFINING_DETAILS,
                    instruction = "Draw a large, sweeping crescent arc wrapping around the body for the iconic bushy tail. Mark two almond shapes for the eyes and a rounded nose tip.",
                    spokenCoachingEn = "Now comes the magical fox tail! Draw a big, generous crescent curve sweeping from behind the body toward the front paws. Add two calm almond eyes on the face.",
                    spokenCoachingHinglish = "Ab fox ki sabse sundar cheez—badi bushy tail! Peeth ke peeche se aage ki taraf ek bada curve banayein. Aur face par do badam ke aakar ki aankhein banayein.",
                    tips = listOf("The tail is thickest in the middle and tapers at the white tip", "Leave eye highlight spots unshaded"),
                    strokeDiagramDesc = "Sweeping crescent tail shape wrapping the side",
                    targetGeometricShapes = listOf("Crescent tail", "Almond eye shapes", "Nose dot")
                ),
                DrawingStep(
                    stepIndex = 5,
                    title = "Fur Tufts & Accent Shading",
                    phase = StepPhase.SHADING_AND_POLISH,
                    instruction = "Darken the primary silhouettes. Add jagged zig-zag strokes for the cheek fur, ear fluff, and tail tip. Add light shadow hatching under the chin and paws.",
                    spokenCoachingEn = "Final touch! Clean up the stray guide lines. Add quick, flicking strokes for fluffy cheek fur and the white tail tip. Shade gently beneath the body for depth!",
                    spokenCoachingHinglish = "Aakhiri step! Extra guide lines ko halka erase karein. Gaal aur kaano par halki fur ke strokes banayein, aur paws ke neeche halka shadow daalein. Aapka sketch tayar hai!",
                    tips = listOf("Use confident, deliberate strokes for the final outline", "Shade in one consistent diagonal direction"),
                    strokeDiagramDesc = "Cheek fur spikes, tail tip zigzag line, floor contact shadow",
                    targetGeometricShapes = listOf("Fur flick strokes", "Ground shadow")
                )
            )
        ),
        DrawingSubject(
            id = "coffee_cup",
            title = "Artisan Coffee Mug",
            category = "Still Life",
            difficulty = "Beginner",
            estimatedMinutes = 12,
            drawableResId = R.drawable.ref_coffee,
            description = "Master perspective ellipses, symmetry, and smooth metallic/ceramic gradient shading.",
            steps = listOf(
                DrawingStep(
                    stepIndex = 1,
                    title = "Top Rim Ellipse",
                    phase = StepPhase.GEOMETRIC_BASES,
                    instruction = "Draw a smooth horizontal ellipse for the cup rim. Ensure the curved ends are rounded like football ends, not sharp pointed corners.",
                    spokenCoachingEn = "Let's practice the king of still life: the ellipse! In the upper center, sketch a smooth horizontal ellipse. Keep turning your wrist to keep the curves symmetrical.",
                    spokenCoachingHinglish = "Chaliye ek perfect ellipse banate hain! Page ke center me ek smooth horizontal oval/ellipse draw karein. Kinaaro ko sharp mat karein, gol rakhein.",
                    tips = listOf("Draw with your whole arm moving from the shoulder", "Avoid pointed lemon tips on the sides"),
                    strokeDiagramDesc = "Horizontal symmetrical ellipse (width:height approx 3:1)",
                    targetGeometricShapes = listOf("Horizontal Ellipse", "Horizontal symmetry axis")
                ),
                DrawingStep(
                    stepIndex = 2,
                    title = "Cup Cylinder & Base",
                    phase = StepPhase.SECONDARY_GUIDES,
                    instruction = "From the widest points of the top ellipse, draw two downward vertical lines that taper slightly inward. Connect them with a curved bottom matching the top rim arc.",
                    spokenCoachingEn = "Great ellipse! Now drop two straight lines down from each side, angling inwards slightly. Close the bottom with a curved smile line that mimics the front of the rim.",
                    spokenCoachingHinglish = "Boht badhiya! Ab dono kinaro se do seedhi lines neeche draw karein jo halki si andar ki taraf jhuki hon. Bottom ko ek curved line se band karein.",
                    tips = listOf("The bottom curve must be just as curved or slightly deeper than the top ellipse"),
                    strokeDiagramDesc = "Tapered cylinder body with bottom curve",
                    targetGeometricShapes = listOf("Two vertical guidelines", "Bottom curved arc")
                ),
                DrawingStep(
                    stepIndex = 3,
                    title = "Saucer Ellipse & Handle",
                    phase = StepPhase.PRIMARY_OUTLINES,
                    instruction = "Draw a wider, flatter ellipse underneath the cup for the saucer plate. On the right side of the cup, draw a smooth ear-shaped double-C curve for the handle.",
                    spokenCoachingEn = "Looking solid! Let's give it a plate and handle. Sketch a wider ellipse underneath the base. Then draw a comfortable double-C curve on the right for the handle.",
                    spokenCoachingHinglish = "Badiya shape! Ab cup ke neeche ek bada ellipse saucer (plate) ke liye banayein. Aur right side me 'C' shape ka ek handle draw karein.",
                    tips = listOf("The handle joins near the top rim and finishes about halfway down the cup body"),
                    strokeDiagramDesc = "Wide saucer ellipse and dual-contour handle",
                    targetGeometricShapes = listOf("Wide base ellipse", "Double C-arc handle")
                ),
                DrawingStep(
                    stepIndex = 4,
                    title = "Liquid Surface & Steam Wisps",
                    phase = StepPhase.DEFINING_DETAILS,
                    instruction = "Inside the top rim, draw a smaller concentric ellipse for the coffee surface. Above the cup, draw two gentle, flowing S-curve wisps for rising steam.",
                    spokenCoachingEn = "Let's pour in the fresh coffee! Draw a smaller concentric ellipse just inside the rim for the liquid level. Add two wavy vertical steam lines rising into the air.",
                    spokenCoachingHinglish = "Ab cup me coffee daalte hain! Rim ke andar ek chhota ellipse draw karein. Aur cup ke upar do wavy 'S' lines banayein garam bhaap ke liye.",
                    tips = listOf("Liquid line should be parallel to the rim curve"),
                    strokeDiagramDesc = "Concentric liquid ellipse and two wavy steam curves",
                    targetGeometricShapes = listOf("Inner liquid ellipse", "Wavy steam lines")
                ),
                DrawingStep(
                    stepIndex = 5,
                    title = "Core Shadow & Rim Highlight",
                    phase = StepPhase.SHADING_AND_POLISH,
                    instruction = "Assume light comes from the top left. Add soft vertical curved hatching on the right side of the cylinder. Darken the inside rim on the left.",
                    spokenCoachingEn = "Time for realistic depth! Light is coming from the left, so shade the right third of the cup with smooth curved pencil strokes. Leave a crisp white highlight on the rim!",
                    spokenCoachingHinglish = "Last step—realism lana! Left side se light aa rahi hai, isliye right side par halki curved shading karein. Saucer par cup ki shadow daalein.",
                    tips = listOf("Follow the curvature of the cylinder when hatching", "Keep highlights clean"),
                    strokeDiagramDesc = "Right-side curved core shadow, drop shadow on saucer",
                    targetGeometricShapes = listOf("Cylinder core shadow", "Drop shadow on plate")
                )
            )
        ),
        DrawingSubject(
            id = "anime_portrait",
            title = "Anime / Manga Face",
            category = "Anime",
            difficulty = "Intermediate",
            estimatedMinutes = 20,
            drawableResId = null,
            description = "Learn the famous Loomis/Anime head construction method, facial proportion crosshairs, and expressive eyes.",
            steps = listOf(
                DrawingStep(
                    stepIndex = 1,
                    title = "Head Sphere & Jaw Wedge",
                    phase = StepPhase.GEOMETRIC_BASES,
                    instruction = "Draw a circle for the cranial ball. Add a vertical center line dropping down, and connect two diagonal lines meeting at a pointed chin.",
                    spokenCoachingEn = "Welcome to anime proportions! Start with a clean circle for the skull. Draw a vertical line right through the center, continuing down to mark where the chin will be.",
                    spokenCoachingHinglish = "Anime style drawing me swagat hai! Ek clean circle banayein skull ke liye. Beech me se ek vertical line neeche draw karein jahan chin aayegi.",
                    tips = listOf("The jaw lines start at the lower quarters of the circle", "Chin point sits below the circle by about half its radius"),
                    strokeDiagramDesc = "Circle with downward V-jaw and center vertical axis",
                    targetGeometricShapes = listOf("Cranial Circle", "Center vertical axis", "V-shaped jawline")
                ),
                DrawingStep(
                    stepIndex = 2,
                    title = "Eye Line & Feature Guidelines",
                    phase = StepPhase.SECONDARY_GUIDES,
                    instruction = "Draw a horizontal guideline across the lower third of the circle for the eye level. Mark small tick lines for the nose tip and mouth.",
                    spokenCoachingEn = "Next, mark our facial guidelines! Draw a horizontal line across the lower third of the circle. This is where the big expressive eyes will rest.",
                    spokenCoachingHinglish = "Ab facial proportions set karte hain. Circle ke lower part me ek horizontal line banayein aankhon ke liye. Nose aur mouth ke liye chhota mark lagayein.",
                    tips = listOf("Keep the space between the two eyes equal to the width of one eye"),
                    strokeDiagramDesc = "Horizontal eye line across lower hemisphere with nose tick",
                    targetGeometricShapes = listOf("Horizontal eye line", "Nose and mouth guide ticks")
                ),
                DrawingStep(
                    stepIndex = 3,
                    title = "Expressive Upper Eyelash Arcs",
                    phase = StepPhase.PRIMARY_OUTLINES,
                    instruction = "Draw two thick, sweeping curved strokes on the eye line for the upper eyelashes. Add the lower lash line and iris ovals.",
                    spokenCoachingEn = "Time for the iconic eyes! Draw two bold, confident arched strokes on the horizontal line for the upper lashes. Then sketch tall vertical ovals for the irises.",
                    spokenCoachingHinglish = "Ab anime ki pehchan—khoobsurat aankhein! Eye line par do bold curved strokes lagayein lashes ke liye. Aur andar do lambe ovals irises ke liye banayein.",
                    tips = listOf("Anime eyes are tall and curved", "Make the upper lash stroke noticeably thicker than the lower"),
                    strokeDiagramDesc = "Two thick arched lash wings and inner tall ovals",
                    targetGeometricShapes = listOf("Thick arched lash lines", "Iris tall ovals")
                ),
                DrawingStep(
                    stepIndex = 4,
                    title = "Hair Bangs & Volume Silhouette",
                    phase = StepPhase.DEFINING_DETAILS,
                    instruction = "Draw large triangular clumps of hair draping over the forehead. Remember that hair sits above the skull circle with air and volume.",
                    spokenCoachingEn = "Let's draw hair with volume! Do not draw individual hair strands; instead, draw confident triangular clumps that frame the face and drape over the forehead.",
                    spokenCoachingHinglish = "Ab hair banate hain! Ek-ek baal banane ke bajaye bade-bade triangular clumps banayein. Yaad rahe baal skull se thoda upar uthe huye hone chahiye.",
                    tips = listOf("Draw hair in ribbons or clumps with varying sizes", "Gravity pulls the bangs down toward the eyes"),
                    strokeDiagramDesc = "Hair clumps flowing around head with top volume dome",
                    targetGeometricShapes = listOf("Triangular hair clumps", "Outer hair dome")
                ),
                DrawingStep(
                    stepIndex = 5,
                    title = "Eye Sparkles & Neck Shading",
                    phase = StepPhase.SHADING_AND_POLISH,
                    instruction = "Preserve two round white highlight spots in each eye. Shade the top of the iris dark, and add a soft shadow beneath the chin on the neck.",
                    spokenCoachingEn = "Bring the character to life! Leave crisp white circles for eye sparkles. Darken the pupil and top of the iris, then shade beneath the chin for instant depth!",
                    spokenCoachingHinglish = "Kamaal ka sketch! Aankhon me do chhote safed circles chhod kar sparkle banayein, aur iris ko upar se dark shade karein. Neck par halki shadow daalein!",
                    tips = listOf("The eye sparkles catch the soul of the drawing", "Keep chin clean and sharp"),
                    strokeDiagramDesc = "Eye pupil gradient, double highlight dots, neck drop shadow",
                    targetGeometricShapes = listOf("Circular eye highlights", "Neck shadow")
                )
            )
        )
    )
}
