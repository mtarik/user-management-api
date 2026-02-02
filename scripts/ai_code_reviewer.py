#!/usr/bin/env python3
"""
AI Code Reviewer for Java using Claude API
Analyzes Java code changes and provides intelligent feedback
"""

import os
import sys
import json
from pathlib import Path
from typing import List, Dict, Optional
import anthropic
import requests
from datetime import datetime


class JavaCodeReviewer:
    """AI-powered code reviewer for Java projects"""

    def __init__(self):
        self.api_key = os.getenv('ANTHROPIC_API_KEY')
        self.github_token = os.getenv('GITHUB_TOKEN')
        self.pr_number = os.getenv('PR_NUMBER')
        self.repo_name = os.getenv('REPO_NAME')
        self.commit_sha = os.getenv('COMMIT_SHA')

        if not self.api_key:
            raise ValueError("ANTHROPIC_API_KEY is not set")

        self.client = anthropic.Anthropic(api_key=self.api_key)
        self.model = "claude-sonnet-4-5-20250929"

    def read_changed_files(self) -> List[Dict[str, str]]:
        """Read all changed Java files"""
        changed_files = []

        if not Path('changed_files.txt').exists():
            print("No changed files found")
            return changed_files

        with open('changed_files.txt', 'r') as f:
            file_paths = [line.strip() for line in f if line.strip()]

        for file_path in file_paths:
            if Path(file_path).exists():
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    changed_files.append({
                        'path': file_path,
                        'content': content,
                        'lines': len(content.splitlines())
                    })
                    print(f"✓ Loaded: {file_path} ({len(content.splitlines())} lines)")
                except Exception as e:
                    print(f"✗ Error reading {file_path}: {e}")

        return changed_files

    def analyze_code_with_ai(self, files: List[Dict[str, str]]) -> Dict:
        """Analyze code using Claude AI"""

        if not files:
            return {
                'summary': 'No Java files to review',
                'files': [],
                'overall_score': 100
            }

        # Prepare the code context
        code_context = "\n\n".join([
            f"### File: {f['path']}\n```java\n{f['content']}\n```"
            for f in files
        ])

        prompt = f"""Tu es un expert en revue de code Java. Analyse le code Java suivant et fournis un rapport détaillé.

{code_context}

Pour chaque fichier, analyse:

1. **Qualité du Code** (0-10):
   - Respect des conventions Java (naming, formatting)
   - Lisibilité et maintenabilité
   - Complexité et architecture

2. **Bugs Potentiels et Erreurs**:
   - Null pointer exceptions potentielles
   - Fuites mémoire
   - Gestion incorrecte des exceptions
   - Problèmes de concurrence (race conditions, deadlocks)

3. **Sécurité**:
   - Injections SQL
   - XSS et autres vulnérabilités OWASP
   - Gestion des données sensibles
   - Validation des entrées

4. **Performance**:
   - Opérations coûteuses
   - Optimisations possibles
   - Utilisation inefficace des collections

5. **Bonnes Pratiques Java**:
   - Utilisation des streams et lambdas
   - Gestion des ressources (try-with-resources)
   - Immutabilité quand approprié
   - Design patterns appropriés

6. **Tests**:
   - Code testable?
   - Couverture suggérée

Fournis la réponse en JSON avec cette structure:
{{
  "summary": "Résumé général de la revue",
  "overall_score": <score 0-100>,
  "files": [
    {{
      "path": "chemin/fichier.java",
      "score": <0-10>,
      "issues": [
        {{
          "severity": "critical|high|medium|low|info",
          "category": "bug|security|performance|style|best-practice",
          "line": <numéro de ligne ou null>,
          "title": "Titre court",
          "description": "Description détaillée",
          "suggestion": "Comment corriger"
        }}
      ],
      "strengths": ["Points positifs..."],
      "recommendations": ["Recommandations générales..."]
    }}
  ]
}}

Sois précis, constructif et fournis des exemples de code quand pertinent."""

        try:
            print(f"\n🤖 Analyzing code with Claude ({self.model})...\n")

            message = self.client.messages.create(
                model=self.model,
                max_tokens=16000,
                temperature=0.3,
                messages=[{
                    "role": "user",
                    "content": prompt
                }]
            )

            response_text = message.content[0].text

            # Extract JSON from response
            json_start = response_text.find('{')
            json_end = response_text.rfind('}') + 1

            if json_start >= 0 and json_end > json_start:
                json_str = response_text[json_start:json_end]
                review_data = json.loads(json_str)
                return review_data
            else:
                return {
                    'summary': 'Error parsing AI response',
                    'raw_response': response_text,
                    'files': []
                }

        except Exception as e:
            print(f"✗ Error during AI analysis: {e}")
            return {
                'summary': f'Error during analysis: {str(e)}',
                'files': [],
                'error': str(e)
            }

    def generate_markdown_report(self, review: Dict, compact: bool = False) -> str:
        """Generate a markdown report from the review
        
        Args:
            review: The review data
            compact: If True, generates a compact version for PR comments
        """

        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        
        # Compact version for PR comments
        if compact:
            md = f"""**📊 Score Global**: {review.get('overall_score', 'N/A')}/100

**🔍 Résumé**: {review.get('summary', 'Aucun résumé disponible')}

"""
        else:
            md = f"""# 🤖 AI Code Review Report - Java

**Date**: {timestamp}
**Modèle**: {self.model}
**Commit**: `{self.commit_sha[:8] if self.commit_sha else 'N/A'}`

---

## 📊 Résumé Général

{review.get('summary', 'Aucun résumé disponible')}

**Score Global**: {review.get('overall_score', 'N/A')}/100

---

"""

        for file_review in review.get('files', []):
            md += f"""## 📄 Fichier: `{file_review['path']}`

**Score**: {file_review.get('score', 'N/A')}/10

"""

            # Issues
            issues = file_review.get('issues', [])
            if issues:
                md += "### 🔍 Problèmes Détectés\n\n"

                # Group by severity
                severity_order = ['critical', 'high', 'medium', 'low', 'info']
                severity_icons = {
                    'critical': '🔴',
                    'high': '🟠',
                    'medium': '🟡',
                    'low': '🔵',
                    'info': 'ℹ️'
                }
                severity_labels = {
                    'critical': 'CRITIQUE',
                    'high': 'HAUT',
                    'medium': 'MOYEN',
                    'low': 'BAS',
                    'info': 'INFO'
                }

                for severity in severity_order:
                    severity_issues = [i for i in issues if i.get('severity') == severity]
                    if severity_issues:
                        md += f"\n#### {severity_icons.get(severity, '•')} {severity_labels.get(severity, severity.upper())}\n\n"
                        for issue in severity_issues:
                            line_info = f" (Ligne {issue['line']})" if issue.get('line') else ""
                            category = issue.get('category', 'general').replace('-', ' ').title()
                            md += f"**[{category}]{line_info}** - {issue.get('title', 'Problème')}\n\n"
                            md += f"{issue.get('description', 'Aucune description')}\n\n"
                            if issue.get('suggestion'):
                                md += f"💡 **Suggestion**: {issue['suggestion']}\n\n"
                            md += "---\n\n"

            # Strengths
            strengths = file_review.get('strengths', [])
            if strengths:
                md += "### ✅ Points Forts\n\n"
                for strength in strengths:
                    md += f"- {strength}\n"
                md += "\n"

            # Recommendations
            recommendations = file_review.get('recommendations', [])
            if recommendations:
                md += "### 💡 Recommandations\n\n"
                for rec in recommendations:
                    md += f"- {rec}\n"
                md += "\n"

            md += "\n---\n\n"

        # Footer only for full reports
        if not compact:
            md += """
## 🔧 Prochaines Étapes

1. 🔴 Corriger les problèmes **CRITIQUE** et **HAUT** en priorité
2. ✅ Appliquer les suggestions proposées
3. 🧪 Exécuter les tests pour vérifier qu'il n'y a pas de régression
4. 📚 Considérer les recommandations pour améliorer la qualité du code

---

*Généré par AI Code Reviewer avec Claude Sonnet 4.5*
"""

        return md

    def post_comment_to_pr(self, comment: str) -> bool:
        """Post a comment to the GitHub PR"""

        if not self.pr_number or not self.github_token:
            print("⚠️  Cannot post to PR: PR_NUMBER or GITHUB_TOKEN not set")
            return False

        url = f"https://api.github.com/repos/{self.repo_name}/issues/{self.pr_number}/comments"
        headers = {
            "Authorization": f"token {self.github_token}",
            "Accept": "application/vnd.github.v3+json"
        }

        data = {"body": comment}

        try:
            response = requests.post(url, headers=headers, json=data)
            if response.status_code == 201:
                print("✓ Comment posted to PR successfully")
                return True
            else:
                print(f"✗ Failed to post comment: {response.status_code}")
                print(response.text)
                return False
        except Exception as e:
            print(f"✗ Error posting comment: {e}")
            return False

    def run(self):
        """Main execution flow"""

        print("=" * 60)
        print("🤖 AI Code Reviewer for Java - Starting...")
        print("=" * 60)

        # Read changed files
        print("\n📂 Reading changed Java files...")
        files = self.read_changed_files()

        if not files:
            print("\n✓ No Java files changed - skipping review")
            return

        print(f"\n📊 Found {len(files)} Java file(s) to review")

        # Analyze with AI
        review = self.analyze_code_with_ai(files)

        # Generate reports
        print("\n📝 Génération des rapports...")

        # Save JSON
        json_file = f"code_review_{self.commit_sha[:8] if self.commit_sha else 'latest'}.json"
        with open(json_file, 'w', encoding='utf-8') as f:
            json.dump(review, f, indent=2, ensure_ascii=False)
        print(f"✓ Rapport JSON sauvegardé: {json_file}")

        # Save full Markdown report
        md_report = self.generate_markdown_report(review, compact=False)
        md_file = f"code_review_{self.commit_sha[:8] if self.commit_sha else 'latest'}.md"
        with open(md_file, 'w', encoding='utf-8') as f:
            f.write(md_report)
        print(f"✓ Rapport Markdown complet sauvegardé: {md_file}")
        
        # Generate compact version for PR comments
        if self.pr_number:
            compact_report = self.generate_markdown_report(review, compact=True)
            compact_file = "review_report.md"
            with open(compact_file, 'w', encoding='utf-8') as f:
                f.write(compact_report)
            print(f"✓ Rapport compact pour PR sauvegardé: {compact_file}")

        # Post to PR if applicable
        if self.pr_number:
            print("\n💬 Posting review to Pull Request...")
            self.post_comment_to_pr(md_report)

        # Print summary
        print("\n" + "=" * 60)
        print("📊 RÉSUMÉ DE LA REVUE")
        print("=" * 60)
        print(f"Score Global: {review.get('overall_score', 'N/A')}/100")
        print(f"\n{review.get('summary', 'Aucun résumé disponible')}")
        print("=" * 60)

        # Exit with warning if critical issues found
        critical_count = sum(
            len([i for i in f.get('issues', []) if i.get('severity') == 'critical'])
            for f in review.get('files', [])
        )

        if critical_count > 0:
            print(f"\n⚠️ AVERTISSEMENT: {critical_count} problème(s) critique(s) détecté(s)!")
            print("🔴 Veuillez corriger ces problèmes avant de merger.")
            # Don't fail the build, just warn


if __name__ == "__main__":
    try:
        reviewer = JavaCodeReviewer()
        reviewer.run()
    except Exception as e:
        print(f"\n✗ Fatal error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
