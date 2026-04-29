require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { PrismaClient } = require('@prisma/client');

const prisma = new PrismaClient();
const app = express();

app.use(cors());
app.use(express.json());

// 1. Get all candidates
app.get('/api/candidates', async (req, res) => {
    try {
        const candidates = await prisma.candidate.findMany({
            orderBy: { id: 'asc' }
        });
        res.json(candidates);
    } catch (error) {
        console.error("Error fetching candidates:", error);
        res.status(500).json({ error: "Internal server error" });
    }
});

// 2. Submit a vote
app.post('/api/vote', async (req, res) => {
    const { voterId, candidateId } = req.body;

    if (!voterId || candidateId == null) {
        return res.status(400).json({ error: "voterId and candidateId are required" });
    }

    try {
        // Use an interactive transaction to ensure atomicity
        const result = await prisma.$transaction(async (tx) => {
            // Find the voter
            const voter = await tx.user.findUnique({
                where: { voterId: voterId }
            });

            if (!voter) {
                throw new Error("Voter not found");
            }

            if (voter.hasVoted) {
                throw new Error("Voter has already voted");
            }

            // Find the candidate
            const candidate = await tx.candidate.findUnique({
                where: { id: candidateId }
            });

            if (!candidate) {
                throw new Error("Candidate not found");
            }

            // Update voter
            await tx.user.update({
                where: { voterId: voterId },
                data: { hasVoted: true }
            });

            // Increment candidate vote count
            await tx.candidate.update({
                where: { id: candidateId },
                data: { voteCount: candidate.voteCount + 1 }
            });

            return { message: "Vote successfully recorded" };
        });

        res.status(200).json(result);
    } catch (error) {
        console.error("Error casting vote:", error);
        // If error message is thrown from within transaction, return it as 400
        if (error.message === "Voter not found" || error.message === "Voter has already voted" || error.message === "Candidate not found") {
            return res.status(400).json({ error: error.message });
        }
        res.status(500).json({ error: "Internal server error" });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
