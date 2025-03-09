package com.alexvanmatre.movieyearsuffixer

import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata

fun main(args: Array<String>) {
	if(args.size != 1) {
		throw Exception("Directory location required")
	}

	val dirPath = args[0]
	val loc = File(dirPath)
	if(!loc.exists()) {
		throw Exception("File location $dirPath does not exist")
	}

	try {
		Files.walk(Paths.get(dirPath), 1)
			.filter { file ->
				!Files.isDirectory(file)
					&& file.name.endsWith(".mp4")
					&& !file.name.split(".")[0].endsWith("}")
					&& !file.name.split(".")[0].endsWith(")")
			}
			.forEach { renameFile(it.toFile()) }
	} catch (e: Exception) {
		e.printStackTrace()
	}
}

private fun renameFile(file: File) {
	val year = grabYearFrom(file)

	if(year != "") {
		val oldPath = file.absolutePath.split('.').first()
		val newPath = "$oldPath ($year).mp4"
		val newFile = File(newPath)

		file.renameTo(newFile)

		println(file.absolutePath + " -> " + newPath)
	}
}

private fun grabYearFrom(file: File): String {
	val tika = Tika()
	val metadata = Metadata()

	FileInputStream(file).use { inputStream ->
		tika.parse(inputStream, metadata)
	}

	return metadata.get("xmpDM:releaseDate") ?: ""
}
