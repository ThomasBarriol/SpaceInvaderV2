package com.info.spaceinvaderv2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.*
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class SpaceView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr : Int = 0): SurfaceView(context, attributes, defStyleAttr), Runnable {
    // Initialisation d'un point qui sera composer de la largeur de l'écran en x, et la hauteur en y
    val displayMetrics = DisplayMetrics()
    private var w: Int = context.resources.displayMetrics.widthPixels
    private var h: Int = context.resources.displayMetrics.heightPixels

    // Initialisation des composants principaux qui permettent la gestion graphique du jeu
    private var thread: Thread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()
    private var bitmapInvader: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader)
    private var bitmapMiniBoss: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader_miniboss)
    private var bitmapBonus: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invader_soucoupe)
    private var bitmapPlayer: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.joueur)
    private val activity = context as FragmentActivity
    private val random: Random = Random()

    // Initialisation des variables qui controlent le message indiquant une nouvelle vague
    private var newVagueMess : Boolean = true
    private var timeElapsedMess : Double = 0.0
    private val timeMess : Double = 2.0

    // Initialisation des variables booléennes qui controleront le fait de jouer ou d'être en pause
    var playing : Boolean = true
    var paused : Boolean = false
    private var gameOver : Boolean = false

    // Initialisation du joueur
    private val timeImmune : Double = 1.0
    private var timeElapsedImmune: Double = 0.0
    private var widthPlayer : Float = w/6f
    private var heightPlayer : Float = h/10f
    private var player: ShipJoueur = ShipJoueur(w, h, widthPlayer, heightPlayer)

    // Initialisation des invaders
    private var invaders = ArrayList<Invader>()
    private val numInvadersInit: Int = 2
    private var numInvaders: Int = numInvadersInit
    private var widthInvader : Int = w/8
    private var heightInvader : Int = h/15
    private var widthMiniBoss: Int = w/5
    private var heightMiniBoss: Int = h/10
    private var widthBonus : Int = w/12
    private var heightBonus : Int = h/20

    // Initialisation des bullets du joueur
    private var playerBullets = ArrayList<Bullet>()
    private var timeBetweenShots : Float = 0.9f
    private var timeElapsedShoot: Double = 0.0

    // Initialisation des bullets ennemies
    private var invadersBullets= ArrayList<Bullet>()

    // Initialisation des variables générales spécifiques à notre jeu
    private var score: Int = 0
    private var viesinit: Int = 2
    private var vies: Int = viesinit
    private var vague: Int = 1
    private var bonus : Int = 1

    //Initialisation des paramètres pour le son
    val soundPool: SoundPool
    val soundMap: SparseIntArray

    init {
        backgroundPaint.color = Color.BLACK

        bitmapInvader = Bitmap.createScaledBitmap(bitmapInvader, widthInvader, heightInvader, false)
        bitmapMiniBoss = Bitmap.createScaledBitmap(bitmapMiniBoss, widthMiniBoss,heightMiniBoss, false)
        bitmapPlayer = Bitmap.createScaledBitmap(bitmapPlayer, widthPlayer.toInt(), heightPlayer.toInt(), false)
        bitmapBonus = Bitmap.createScaledBitmap(bitmapBonus, widthBonus, heightBonus, false)
        initialisationNiveau(vague)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap = SparseIntArray(4)
        soundMap.put(0, soundPool.load(context, R.raw.destruction, 2))
        soundMap.put(1, soundPool.load(context, R.raw.pew, 1))
        soundMap.put(2, soundPool.load(context, R.raw.hit, 3))
        soundMap.put(3, soundPool.load(context, R.raw.lose, 1))
    }

    override fun run(){
        var previousFrameTime = System.currentTimeMillis()
        while (playing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS:Double=(currentTime-previousFrameTime).toDouble()
            timeElapsedShoot += elapsedTimeMS / 1000.0
            timeElapsedImmune += elapsedTimeMS /1000.0
            timeElapsedMess += elapsedTimeMS / 1000.0
            if (!paused){
                update_game(elapsedTimeMS/1000.0)
            }
            draw()
            previousFrameTime = currentTime
        }
    }

    private fun draw(){
        if(holder.surface.isValid){
            // On bloque le canvas pour pouvoir dessiner dessus
            canvas = holder.lockCanvas()

            // Dessine le background
            canvas.drawColor(backgroundPaint.color)
            paint.color = Color.WHITE

            // Dessine le joueur
            player.draw(canvas, bitmapPlayer, player.position, paint)

            // Dessine les bullets du joueur si elles sont actives
            for (bullet in playerBullets){
                if (bullet.isActive){
                    bullet.draw(canvas)
                }
            }

            // Dessine les bullets des ennemies si elles sont actives
            for (bullet in invadersBullets){
                if (bullet.isActive){
                    bullet.draw(canvas)
                }
            }

            // Dessine les invaders
            for (invader in invaders){
                if (invader.isVisible)
                    when (invader.type){
                        1 -> invader.draw(canvas, bitmapInvader, invader.position, paint)
                        2 -> invader.draw(canvas, bitmapBonus, invader.position, paint)
                        5 -> {invader.draw(canvas, bitmapMiniBoss, invader.position, paint)
                        paint.textAlign = Paint.Align.CENTER
                        paint.textSize = 60f
                        canvas.drawText("${invader.life}", invader.position.left + widthMiniBoss/2, invader.position.top - 50f, paint)}
                    }

            }

            // On dessine le texte
            paint.textSize = 60f
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("Score : $score     Vies : $vies    Vague : $vague  bonus : ${bonus - 1}", 20f, 75f, paint)

            // Si nouvelle vague, on affiche la vague en grand au milieu pour annoncer la vague n°x
            if (newVagueMess){
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 200f
                canvas.drawText("Vague $vague", w/2f, h/4f, paint)
                if (timeElapsedMess >= timeMess){
                    timeElapsedMess = 0.0
                    newVagueMess = false
                }
            }
            // On dessine tous sur le canvas et on le débloque
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun update_game(fps: Double) {
        // Update tous les composants du jeu

        // update les déplacements du joueur
        player.update()

        // update les invaders, tire si ils en ont la chance et on vérifie si un invader a atteint le bas de la page,
        //  si oui, le joueur a perdu
        for (invader in invaders) {
            if (invader.isVisible && !newVagueMess) {
                invader.updateMove(fps, vague)
                manage_invader_shot(invader)
                if (invader.position.intersect(player.position)) gameOver = true
            }
        }

        // Toutes les secondes, le joueur tire
        if (timeElapsedShoot >= timeBetweenShots && !newVagueMess){
            player.tire(playerBullets, bonus)
            soundPool.play(soundMap.get(1), 0.6f, 0.6f, 1, bonus - 1, 1f)
            timeElapsedShoot = 0.0
        }

        // Update les bullets du joueur et détecte si elles touchent un invader,
        // si oui, enlève une vie à l'invader, augmente le score
        for (bullet in playerBullets){
            check_invader_hit(bullet, fps)
        }

        // On vérifie si les invaders ont touché le joueur, si oui alors il perd une vie et est immunisé
        for (bullet in invadersBullets){
            check_player_is_hit(bullet, fps)
        }

        // Nouvelle vague si le joueur a tué tous les invaders de la vague
        if (numInvaders == 0) {
            playing = false
            newVague()
        }
        // GameOver si il a plus de vie ou si un invader l'a touché
        else if (vies == 0 || gameOver){
            playing = false
            gameOver = false
            showGameOverDialog(R.string.lose)
        }
    }

    private fun check_player_is_hit(bullet: Bullet, fps: Double) {
        if (bullet.isActive) {
            bullet.update(fps)
            if (bullet.position.intersect(player.position) && timeElapsedImmune >= timeImmune) {
                soundPool.play(soundMap.get(2), 1f, 1f, 1, 0, 1f)
                vies--
                bonus = if (bonus == 1) bonus else bonus - 1
                bullet.isActive = false
                timeElapsedImmune = 0.0
            }
        }
    }

    private fun check_invader_hit(bullet: Bullet, fps: Double) {
        if (bullet.isActive) {
            bullet.update(fps)  //update les bullets
            for (invader in invaders) {
                // Si l'invader est actif et que la bullet le touche
                if (invader.isVisible && bullet.position.intersect(invader.position)) {
                    //si il touche un bonus
                    if (invader.type == 2) {
                        invader.isVisible = false
                        bullet.isActive = false
                        bonus = if (bonus <= 3) bonus + 1 else 3
                    }
                    // Si il touche autre chose
                    else {
                        invader.life--
                        bullet.isActive = false
                        score += 10 * invader.type
                    }
                    // Si l'invader n'a plus de vie
                    if (invader.life == 0) {
                        soundPool.play(soundMap.get(0), 1f, 1f, 1, 0, 1f)
                        numInvaders--
                        invader.isVisible = false
                    }
                }
            }
        }
    }

    private fun manage_invader_shot(invader: Invader) {
        if (invader.takeShot(vague)) {
            when (invader.type) {
                1 -> invadersBullets.add(
                    Bullet(h, invader.position.centerX(), invader.position.bottom, 1)
                )
                5 -> {
                    val directionality = invader.shoot(player.position, invader.position)
                    invadersBullets.add(
                        BulletMiniBoss(
                            h,
                            invader.position.centerX(),
                            invader.position.bottom,
                            1, 700f, 30f, directionality
                        )
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_MOVE -> if (event.x >= widthPlayer/2 && event.x <= w - widthPlayer/2){
                player.position.left = event.x - widthPlayer/2
                player.position.right = player.position.left + widthPlayer
            }
        }
        return true
    }

    private fun initialisationNiveau(vague: Int){
        // Si la vague est paire, on rajoute un certains nombre d'invader et un MiniBoss
        if (vague % 2 == 0){
            for (i in 1..numInvaders){
                var moving = random.nextInt(1)
                moving = if (moving == 0) -1 else moving
                invaders.add(Invader((0..w).random().toFloat(),(0..h/4).random().toFloat(), w, h, widthInvader, heightInvader, moving))
            }
            var moving = random.nextInt(1)
            moving = if (moving == 0) -1 else moving
            invaders.add(Miniboss(w/2f, h/4f, w, h, moving, widthMiniBoss, heightMiniBoss ))
            numInvaders ++
        }
        // Sinon on rajoute juste les invaders normaux
        else {
            for (i in 1..numInvaders){
                var moving = random.nextInt(1)
                moving = if (moving == 0) -1 else moving
                invaders.add(Invader((0..w).random().toFloat(),(0..h/4).random().toFloat(), w, h, widthInvader, heightInvader, moving))
            }
        }
        // La vague peut avoir de manière aléatoire, un bonus en plus
        val vagueBonus: Boolean = random.nextInt(3) == 0
        if (vagueBonus){
            var moving = random.nextInt(1)
            moving = if (moving == 0) -1 else moving
            invaders.add(Invader((0..w).random().toFloat(),(0..h/4).random().toFloat(), w, h, widthBonus, heightBonus, moving))
            invaders[numInvaders].type = 2
        }
    }

    private fun showGameOverDialog(messageId: Int) {
        soundPool.play(soundMap.get(3), 1f, 1f, 1, 0, 1f)
        class GameResult: DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage("Score : ${score} \nVague :" +
                        " ${vague}"
                )
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _->newGame()})
                builder.setNegativeButton("menu", DialogInterface.OnClickListener { _, _-> exitProcess(-1) })
                return builder.create()
            }
        }
        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft,"dialog")
            }
        )

    }

    fun newGame(){
        vague = 1
        score = 0
        vies = viesinit
        timeElapsedShoot = 0.0
        timeElapsedImmune = 0.0
        timeElapsedMess = 0.0
        bonus = 1
        numInvaders = numInvadersInit
        playerBullets.clear()
        invadersBullets.clear()
        invaders.clear()
        initialisationNiveau(vague)
        playing = true
        paused = false
        thread = Thread(this)
        thread.start()
    }

    private fun newVague(){
        timeElapsedImmune = 0.0
        timeElapsedMess = 0.0
        playerBullets.clear()
        invadersBullets.clear()
        invaders.clear()
        vies ++
        vague ++
        timeBetweenShots -= 0.05f
        numInvaders = if (vague <= 10) numInvadersInit + vague - 1 else 11
        initialisationNiveau(vague)
        newVagueMess = true
        playing = true
    }

    fun showPauseMenu(messageId: Int){
        playing = false
        paused = true
        class GameResult: DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _->newGame()})
                builder.setNeutralButton("menu", DialogInterface.OnClickListener { _, _-> exitProcess(-1) })
                builder.setNegativeButton("continuer",DialogInterface.OnClickListener {_,_-> resume()} )
                return builder.create()
            }
        }
        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft,"dialog")
            }
        )
    }

    fun pause(){
        playing = false
        paused = true
    }

    fun resume(){
        playing = true
        paused = false
        thread = Thread(this)
        thread.start()
    }
}